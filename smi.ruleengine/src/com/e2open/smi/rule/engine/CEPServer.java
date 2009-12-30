package com.e2open.smi.rule.engine;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.statistic.ProfilerTimerFilter;
import org.apache.mina.integration.jmx.IoFilterMBean;
import org.apache.mina.integration.jmx.IoServiceMBean;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;

import com.e2open.smi.rule.engine.eif.codec.TECEventCodecFactory;
import com.e2open.smi.rule.engine.eif.consume.EventReceiverHandler;
import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.eif.event.Severity;
import com.e2open.smi.rule.engine.eif.event.Slot;
import com.e2open.smi.rule.engine.eif.event.Status;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;
import com.e2open.smi.rule.engine.rules.Rule;
import com.e2open.smi.rule.engine.rules.RuleManager;
import com.e2open.smi.rule.engine.rules.RulePacDefinition;
import com.e2open.smi.rule.problems.ProblemManager;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.metric.EngineMetric;
import com.espertech.esper.client.metric.StatementMetric;

public class CEPServer {
	private static Logger logger = null;
	protected boolean active = false;
	protected String name;
	protected String version;

	protected String hostname;

	private NioSocketAcceptor acceptor;
	private ProtocolCodecFilter eifCODEC;
	private ExecutorService handlerThreadPool = null;
	private boolean cepServerRunning = false;

	private static CEPServer server = new CEPServer();

	private int receivePort = 5500;
	private int executorPoolSize = 32;

	private ArrayList<RulePacDefinition> awaitingRulePacs = new ArrayList<RulePacDefinition>();

	private static Logger devnull = Logger.getLogger("CEPServer");
	static {
		devnull.setLevel(Level.FINEST);
		StreamHandler stdout = new StreamHandler(System.out, new SimpleFormatter());
		stdout.setLevel(Level.FINEST);
		devnull.addHandler(stdout);
	}

	public static Logger getLogger() {
		if (null == logger)
			return devnull;
		else
			return logger;
	}

	public Configuration cfg = new Configuration();

	private void createCEP() {

		cfg.addImport(Event.class.getCanonicalName());
		cfg.addImport(Severity.class.getCanonicalName());
		cfg.addImport(Slot.class.getCanonicalName());
		cfg.addImport(Status.class.getCanonicalName());
		cfg.addImport("com.espertech.esper.client.metric.*");
		cfg.addEventType("Event", Event.class.getName());
		cfg.addEventType("Severity", Severity.class.getName());
		cfg.addEventType("Slot", Slot.class.getName());
		cfg.addEventType("Status", Status.class.getName());
		cfg.addEventType("EngineMetric", EngineMetric.class.getName());
		cfg.addEventType("StatementMetric", StatementMetric.class.getName());
		// cfg.setMetricsReportingEnabled();
		cfg.setMetricsReportingDisabled();
		cfg.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);
		cfg.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
		EPServiceProvider defaultSvc = EPServiceProviderManager.getDefaultProvider(cfg);

		defaultSvc.initialize();

		ruleQueue = new ArrayBlockingQueue<Rule>(100);
		ruleAdder = new RuleAdder(ruleQueue);
		ruleAdder.start();
		ProblemManager.getInstance().init();

		cepServerRunning = true;
		getLogger().log(Level.CONFIG, "CEP Server Running");
	}

	RuleAdder ruleAdder = null;

	static public CEPServer getServer() {
		if (server == null)
			server = new CEPServer();
		return server;
	}

	public void consume(int receivePort, int receivePoolSize) throws IOException {
		getLogger().config("Listening on port " + receivePort + ", executor pool of " + receivePoolSize + " threads");
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

			eifCODEC = new ProtocolCodecFilter(new TECEventCodecFactory(Charset.forName("UTF-8")));
			mbs.registerMBean(new IoFilterMBean(eifCODEC), new ObjectName("eif.server:type=eifCODEC"));

//			handlerThreadPool = Executors.newFixedThreadPool(receivePoolSize);
			 // 1 thread is always available
            // up to a max of receivePoolSize threads
            // kept alive for 60 seconds
//            handlerThreadPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
//			ExecutorFilter pool = new ExecutorFilter(handlerThreadPool);

			// cause out of memory
			ExecutorFilter pool = new ExecutorFilter(receivePoolSize);
			// caused null's
//			ExecutorFilter pool = new ExecutorFilter(new UnorderedThreadPoolExecutor(receivePoolSize));

			mbs.registerMBean(new IoFilterMBean(pool), new ObjectName("eif.server:type=handlerThreadPool"));

			// simple logging to the screen
			// see sl4j.com for using log4j or jdk logging
			// acceptor.getFilterChain().addLast("logger", new LoggingFilter());

			acceptor = new NioSocketAcceptor(); 
			// disable Nagle algorithm
//            acceptor.getSessionConfig().setTcpNoDelay(true);
			mbs.registerMBean(new IoServiceMBean(acceptor), new ObjectName("eif.server:type=acceptor"));

			acceptor.getFilterChain().addLast("threadPool", pool);
			acceptor.getFilterChain().addLast("codec", eifCODEC);

            ProfilerTimerFilter profiler = new ProfilerTimerFilter(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED);
            mbs.registerMBean(new IoFilterMBean(profiler), new ObjectName("eif.server:type=profiler"));

            acceptor.getFilterChain().addLast("Profiler", profiler);
			
			acceptor.setHandler(new EventReceiverHandler());

			acceptor.getSessionConfig().setMaxReadBufferSize(8192);
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			getLogger().fine("Binding to port " + receivePort);
			acceptor.bind(new InetSocketAddress(receivePort));
		} catch (MalformedObjectNameException e) {
			getLogger().warning("MalformedObjectNameException: " + e.getMessage());
		} catch (NullPointerException e) {
			getLogger().warning("NullPointerException: " + e.getMessage());
		} catch (InstanceAlreadyExistsException e) {
			getLogger().warning("InstanceAlreadyExistsException: " + e.getMessage());
		} catch (MBeanRegistrationException e) {
			getLogger().warning("MBeanRegistrationException: " + e.getMessage());
		} catch (NotCompliantMBeanException e) {
			getLogger().warning("NotCompliantMBeanException: " + e.getMessage());
		}
	}

	public void shutdownAndAwaitTermination() {
		acceptor.unbind();
		acceptor.dispose();
		handlerThreadPool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!handlerThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				handlerThreadPool.shutdownNow(); // Cancel currently executing
				// tasks
				// Wait a while for tasks to respond to being cancelled
				if (!handlerThreadPool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
			ruleQueue.add(NO_MORE_WORK);
			EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
			engine.destroy();
		} catch (InterruptedException ie) {
			getLogger().finer("retry shutdown");
			// (Re-)Cancel if current thread also interrupted
			handlerThreadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		} finally {
			getLogger().info("Shutdown MINA");
			cepServerRunning = false;
		}
	}

	public void activate(ComponentContext cc) {
		try {
			String rawHostname = InetAddress.getLocalHost().getHostName();
			hostname = rawHostname.indexOf('.') < 0 ? rawHostname : rawHostname.substring(0, rawHostname.indexOf('.'));
		} catch (UnknownHostException e) {
			if (logger != null)
				logger.warning(e.getLocalizedMessage());
			hostname = "UNKNOWN";
		}
		Bundle b = cc.getBundleContext().getBundle();
		name = b.getSymbolicName();
		version = (String) b.getHeaders().get("Bundle-Version");

		if (logger != null)
			logger.info("activating CEPServer: " + name + " on host " + hostname);

		active = true;
		try {
			CEPServer server = getServer();
			server.createCEP();
			server.consume(receivePort, executorPoolSize);
			for (RulePacDefinition rulePac : getServer().awaitingRulePacs) {
				bindRulePac(rulePac);
			}
			awaitingRulePacs.clear();
		} catch (Throwable e) {
			CEPServer.getLogger().warning("Exception: " + e.getMessage());
		}
	}

	public void deactivate(ComponentContext cc) {
		if (logger != null)
			logger.info("deactivating CEPServer: " + name);
		active = false;
	}

	@SuppressWarnings("static-access")
	protected void bindLogger(Logger logger) {
		this.logger = logger;
		getLogger().log(Level.INFO, "Logger Bound");
	}

	@SuppressWarnings("static-access")
	protected void unbindLogger(Logger logger) {
		getLogger().log(Level.INFO, "Logger Unbound");
		this.logger = null;
	}

	protected void bindRulePac(RulePacDefinition rulePac) {
		if (getServer().isCepServerRunning()) {
			// RuleManager.getInstance().addRulePac(rulePac);
			for (Rule rule : rulePac.getRules()) {
				try {
					getServer().ruleQueue.put(rule);
				} catch (InterruptedException e) {
					getLogger().severe("Interrupted: " + e.getMessage());
				}
			}
		} else {
			getLogger().log(Level.WARNING, "Defering loading of rule pac until CEP Server running: " + rulePac.getName());
			getServer().awaitingRulePacs.add(rulePac);
		}
	}

	protected void unbindRulePac(RulePacDefinition rulePac) {
		if (getServer().isCepServerRunning())
			RuleManager.getInstance().removeRulePac(rulePac);
		else {
			getLogger().log(Level.WARNING, "Unloading rule pac when CEP server not running: " + rulePac.getName());
			getServer().awaitingRulePacs.remove(rulePac);
		}
	}

	protected void bindAction(Action action) {
		ActionMgr.getInstance().addAction(action);
	}

	protected void unbindAction(Action action) {
		ActionMgr.getInstance().removeAction(action);
	}

	protected void bindExpressionResolver(ExpressionResolver expressionResolver) {
		ExpressionResolverMgr.getInstance().addExpressionResolver(expressionResolver);
	}

	protected void unbindExpressionResolver(ExpressionResolver expressionResolver) {
		ExpressionResolverMgr.getInstance().removeExpressionResolver(expressionResolver);
	}

	public boolean isCepServerRunning() {
		return cepServerRunning;
	}

	static final Rule NO_MORE_WORK = new Rule();

	private BlockingQueue<Rule> ruleQueue = null;

	class RuleAdder extends Thread {

		BlockingQueue<Rule> q;

		RuleAdder(BlockingQueue<Rule> q) {
			this.q = q;
		}

		public void run() {
			try {
				while (true) {
					// Retrieve and block if the queue is empty
					Rule rule = q.take();

					// Terminate if the end-of-stream marker was retrieved
					if (rule == NO_MORE_WORK) {
						break;
					}

					RuleManager.getInstance().addRule(rule);
				}
			} catch (InterruptedException e) {
				getLogger().severe("Interrupted: " + e.getMessage());
			}
		}
	}
}
