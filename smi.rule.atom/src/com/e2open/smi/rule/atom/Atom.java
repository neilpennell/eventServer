package com.e2open.smi.rule.atom;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class Atom {
	private static Logger logger = null;
	private String name = "", version = "";
	@SuppressWarnings("unused")
	private boolean active = false;
	private HttpService httpService = null;
	private ArrayList<AtomFeedPublisher> atomFeedPublishers = new ArrayList<AtomFeedPublisher>();

	public void activate(ComponentContext cc) {
		Bundle b = cc.getBundleContext().getBundle();
		name = b.getSymbolicName();
		version = (String) b.getHeaders().get("Bundle-Version");

		if (logger != null)
			logger.info("activating Abdera: " + name);

		getLogger().info(name + " : " + version);
		// add startup code here ...

		active = true;
	}

	private void registerPublisher(AtomFeedPublisher afp) {
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		try {
			Dictionary<String, Object> initParams = afp.getInitParams();
			ClassLoader bundleClassLoader = afp.getClassLoader();
			if (initParams != null)
				thread.setContextClassLoader(bundleClassLoader);

			httpService.registerServlet(afp.getAlias(), afp.getServlet(), initParams, afp.getContext());
			getLogger().log(Level.INFO, "registered AtomFeedPublisher " + afp.getName());
		} catch (ServletException e) {
			getLogger().log(Level.WARNING,
					"Unable to register AtomFeedPublisher: " + afp.getName() + " ServletException =" + e.getMessage());
			e.printStackTrace();
		} catch (NamespaceException e) {
			getLogger().log(Level.WARNING,
					"Unable to register AtomFeedPublisher: " + afp.getName() + " NamespaceException =" + e.getMessage());
		} finally {
			thread.setContextClassLoader(loader);
		}
	}

	private void unregisterPublisher(AtomFeedPublisher afp) {
		httpService.unregister(afp.getAlias());
		getLogger().log(Level.INFO, "unregistered AtomFeedPublisher " + afp.getName());
	}

	protected void bindLogger(Logger logger) {
		Atom.logger = logger;
		getLogger().log(Level.INFO, "Logger Bound");
	}

	protected void unbindLogger(Logger logger) {
		getLogger().log(Level.INFO, "Logger Unbound");
		Atom.logger = null;
	}

	protected void bindFeedPublisher(AtomFeedPublisher afp) {
		getLogger().log(Level.INFO, "AtomFeedPublisher Bound: " + afp.getName());
		atomFeedPublishers.add(afp);
		if (httpService != null) {
			registerPublisher(afp);
		}
	}

	protected void unbindFeedPublisher(AtomFeedPublisher afp) {
		getLogger().log(Level.INFO, "AtomFeedPublisher Unbound: " + afp.getName());
		atomFeedPublishers.remove(afp);
		if (httpService != null) {
		}
	}

	/**
	 * Will register any AtomFeedPublishers that have been queued up.
	 * 
	 * @param httpsvc
	 */
	protected void bindHTTPServer(HttpService httpsvc) {
		this.httpService = httpsvc;
		getLogger().log(Level.INFO, "HTTP Service Bound");
		for (AtomFeedPublisher afp : atomFeedPublishers) {
			registerPublisher(afp);
		}
	}

	protected void unbindHTTPServer(HttpService httpsvc) {
		getLogger().log(Level.INFO, "HTTP Service Unbound");
		for (AtomFeedPublisher afp : atomFeedPublishers) {
			unregisterPublisher(afp);
		}
		this.httpService = null;
	}

	private static Logger devnull = Logger.getLogger("Abdera");
	static {
		devnull.setLevel(Level.FINEST);
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new SingleLineFormatter());

		devnull.addHandler(ch);
		devnull.setUseParentHandlers(false);
	}

	public static Logger getLogger() {
		if (null == logger)
			return devnull;
		else
			return logger;
	}

}
