package com.e2open.smi.rule.pac.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.ho.yaml.Yaml;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import com.e2open.smi.rule.pac.loader.impl.YamlActionInstance;
import com.e2open.smi.rule.pac.loader.impl.YamlRule;
import com.e2open.smi.rule.pac.loader.impl.YamlRulePac;
import com.e2open.smi.rule.pac.loader.locationmonitor.DirectoryScanner;

public class RulePacLoader implements Runnable {
	private String name = "", version = "";
	@SuppressWarnings("unused")
	private boolean active = false;

	private BundleContext bc = null;
	private ServiceRegistration a1 = null;

	private HashMap<String, YamlActionInstance> actors = new HashMap<String, YamlActionInstance>();
	private ScheduledExecutorService stpe = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> sf;
	private DirectoryScanner a;
	// string should be file.getCanonicalPath() to make sure it is unique in the
	// system.
	private HashMap<String, ServiceRegistration> registeredRulePacServices = new HashMap<String, ServiceRegistration>();

	public void activate(ComponentContext cc) {
		Bundle b = cc.getBundleContext().getBundle();
		name = b.getSymbolicName();
		version = (String) b.getHeaders().get("Bundle-Version");

		System.out.println("Bundle Started: " + name + " : " + version);
		// add startup code here ...
		bc = cc.getBundleContext();
		a = new DirectoryScanner(
				"/Users/npennell/Documents/workspace/cep-server-osgi/com.e2open.smi.rule.pac.loader/src/com/e2open/smi/rule/pac/loader",
				new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return (name.startsWith("rp") && name.endsWith(".yaml"));
					}
				});
		sf = stpe.scheduleWithFixedDelay(this, 15, 15, TimeUnit.SECONDS);

		active = true;
	}

	public void deactivate(ComponentContext cc) {
		System.out.println("Bundle STOPPED: " + name);
		active = false;
		sf.cancel(true);
		stpe.shutdown();
		bc.ungetService(a1.getReference());
		a1 = null;
		bc = null;
	}

	public static void main(String[] args) {
		RulePacLoader rpl = new RulePacLoader();
		rpl.createActors("src/com/e2open/smi/rule/pac/loader/actors.yaml");
		rpl.createHBTORulePac("src/com/e2open/smi/rule/pac/loader/rulepac.yaml");
		rpl.createTestRulePac("src/com/e2open/smi/rule/pac/loader/rp-test.yaml");
		// rpl.testRun();

		// HashMap<String, YamlActionInstance> actionInstances;
		// actionInstances =
		// rpl.loadActors("/Users/npennell/Documents/workspace/cep-server-osgi/"
		// +
		// "com.e2open.smi.rule.pac.loader/src/com/e2open/smi/rule/pac/loader/actors.yaml");
		System.out.println("Completed");
	}

	private void createTestRulePac(String filename) {
		try {
			File f = new File(filename);
			YamlRule r1 = new YamlRule("TestRP:dumpWarnEvents", "Display all Warning events to stdout",
					"select * from Event where severity = Severity.WARNING");
			r1.addParallelAction(actors.get("Internal:PrintToStdOut"));

//			YamlRule r2 = new YamlRule("TestRP:testSlot", "when the slot abc exist",
//					"select * from Event where exists(Event.slot('abc'))");
//			r2.addSerialAction(actors.get("Internal:PrintToStdOut"));

			YamlRule r3 = new YamlRule("TestRP:testActionChaining", "Will add slot1, print, add slot2, print",
					"select * from Event where severity = Severity.MINOR");
			r3.addSerialAction(actors.get("Internal:slot1"));
			r3.addSerialAction(actors.get("Internal:PrintToStdOut"));
			r3.addSerialAction(actors.get("Internal:slot2"));
			r3.addSerialAction(actors.get("Internal:PrintToStdOut"));
			r3.addSerialAction(actors.get("Internal:IncreaseSeverityToFatal"));


			YamlRule r5 = new YamlRule("TestRP:HBTOStreamPrint", "print events on the heatbeat stream",
					"select * from heartbeat");
			r5.addSerialAction(actors.get("Internal:PrintToStdOut"));

			YamlRulePac rp = new YamlRulePac("TestRules", "Rules related to testing of functionality");
			rp.addRule(r1);
//			rp.addRule(r2);
			rp.addRule(r3);
			rp.addRule(r5);

			Yaml.dump(rp, f);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

//	private HashMap<String, YamlActionInstance> loadActors(String filename) {
//		File f = new File(filename);
//		HashMap<String, YamlActionInstance> ais = new HashMap<String, YamlActionInstance>();
//		try {
//			ais = (HashMap<String, YamlActionInstance>) Yaml.load(f);
//			actors.clear();
//			actors.putAll(ais);
//		} catch (FileNotFoundException e) {
//			System.out.println(e.getMessage());
//		}
//		return ais;
//	}

	public void createActors(String filename) {
		try {
			File f = new File(filename);
			YamlActionInstance a1 = new YamlActionInstance("AddUpdateProblem", "Internal:HBTOProblem",
					"Create a problem for HBTO that are not clearing events");
			a1.addParameter("CORRELATOR", "&slot.severity; &slot.type; &slot.hostname;");
			a1.addParameter("TITLE", "HBTO for host=&slot.hostname;");
			a1.addParameter("OWNER", "system");
			a1.addParameter("STATUS", "OPEN");
			a1.addParameter("myAttribute", "value for my attribute &slot.severity; &formattedCurrentDateTime;");

			YamlActionInstance a2 = new YamlActionInstance("AddUpdateProblem", "Internal:HBTOCloseProblem",
					"Close a Problem when a clearing event arrives");
			a2.addParameter("CORRELATOR", "&slot.severity; &slot.type; &slot.hostname;");
			a2.addParameter("OWNER", "&slot.owner;");
			a2.addParameter("STATUS", "CLOSED");

			YamlActionInstance a3 = new YamlActionInstance("PrintToStdOut", "Internal:PrintToStdOut", "Writes Event to STDOUT");

			YamlActionInstance a4 = new YamlActionInstance("PrintToStdErr", "Internal:PrintToStdErr", "Writes Event to STDERR");

			YamlActionInstance a5 = new YamlActionInstance("AddUpdateSlotWithValue", "Internal:slot1",
					"creates a slot called slot1");
			a5.addParameter("slot1", "&formattedCurrentDateTime;");

			YamlActionInstance a6 = new YamlActionInstance("AddUpdateSlotWithValue", "Internal:slot2",
					"creates a slot called slot2");
			a6.addParameter("slot2", "&formattedCurrentDateTime;");

			YamlActionInstance a7 = new YamlActionInstance("AddUpdateSlotWithValue", "Internal:IncreaseSeverityToFatal",
					"Changes the severity to FATAL");
			a7.addParameter("severity", "FATAL");

			actors.put(a1.getName(), a1);
			actors.put(a2.getName(), a2);
			actors.put(a3.getName(), a3);
			actors.put(a4.getName(), a4);
			actors.put(a5.getName(), a5);
			actors.put(a6.getName(), a6);
			actors.put(a7.getName(), a7);
			Yaml.dump(actors, f);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public void createHBTORulePac(String filename) {
		try {
			File f = new File(filename);
			YamlRule r0 = new YamlRule("TestRP:HBTOStream", "insert HBTO events into a new stream called heartbeat",
			"insert into heartbeat select * from Event(type = 'HBTO')");

			YamlRule r1 = new YamlRule("HBTO:CreateProblem", "Create a problem for HBTO that are not clearing events",
					"select * from Event(type = 'HBTO' and status != Status.CLEARING)");
			r1.addSerialAction(actors.get("Internal:HBTOProblem"));

			YamlRule r2 = new YamlRule("HBTO:CloseClearedProblem", "Closes a problem when it clears",
					"select * from Event(type = 'HBTO' and status = Status.CLEARING)");
			r2.addSerialAction(actors.get("Internal:HBTOCloseProblem"));

			YamlRule r3 = new YamlRule("HBTO:CountEvents", "Print Events per second for HBTO events to stdout",
					"select count(*) as cnt from Event.win:time_batch(1 second) where type = 'HBTO'");
			r3.addParallelAction(actors.get("Internal:PrintToStdOut"));

			YamlRulePac rp = new YamlRulePac("HBTORules", "Rules related to the handling of Heartbeat timeouts");
			rp.addRule(r0);
			rp.addRule(r1);
			rp.addRule(r2);
			rp.addRule(r3);

			Yaml.dump(rp, f);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	public void load() {
		try {
			File f = new File("src/com/e2open/smi/rule/pac/loader/actorsExample.yaml");
			System.out.println(f.exists());
			Object mydata;
			mydata = Yaml.load(f);
			System.out.println(mydata);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	public void run() {
		HashMap<String, ArrayList<File>> scanResults = a.scan();

//		System.out.println("new=" + scanResults.get(DirectoryScanner.NEW));
//		System.out.println("changed=" + scanResults.get(DirectoryScanner.CHANGED));
//		System.out.println("deleted=" + scanResults.get(DirectoryScanner.DELETED));
//		System.out.println("----------------------");

		try {
			// add new rule pacs
			for (File newFile : scanResults.get(DirectoryScanner.NEW)) {
				YamlRulePac rp = (YamlRulePac) Yaml.load(newFile);
				RulePacDefWrapper rpdw = new RulePacDefWrapper(rp);
				ServiceRegistration sr = bc.registerService("com.e2open.smi.rule.engine.rules.RulePacDefinition", rpdw, null);
				registeredRulePacServices.put(newFile.getCanonicalPath(), sr);
			}
			// remove deleted rulepacs
			for (File deletedFile : scanResults.get(DirectoryScanner.DELETED)) {
				ServiceRegistration sr = registeredRulePacServices.get(deletedFile.getCanonicalPath());
				sr.unregister();
			}
			// update rule pacs
			for (File changedFile : scanResults.get(DirectoryScanner.CHANGED)) {
				ServiceRegistration sr = registeredRulePacServices.get(changedFile.getCanonicalPath());
				sr.unregister();
				YamlRulePac rp = (YamlRulePac) Yaml.load(changedFile);
				RulePacDefWrapper rpdw = new RulePacDefWrapper(rp);
				sr = bc.registerService("com.e2open.smi.rule.engine.rules.RulePacDefinition", rpdw, null);
				registeredRulePacServices.put(changedFile.getCanonicalPath(), sr);
			}

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
}
