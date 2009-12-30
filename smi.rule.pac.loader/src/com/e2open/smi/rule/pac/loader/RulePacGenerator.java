package com.e2open.smi.rule.pac.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.ho.yaml.Yaml;

import com.e2open.smi.rule.action.builtin.ResendEvent;
import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.pac.loader.impl.YamlActionInstance;
import com.e2open.smi.rule.pac.loader.impl.YamlRule;
import com.e2open.smi.rule.pac.loader.impl.YamlRulePac;

public class RulePacGenerator {
	private HashMap<String, YamlActionInstance> actors = new HashMap<String, YamlActionInstance>();

	public static void main(String[] args) {
		String filelocation = "./";
		if (args.length > 0) {
			filelocation = args[0];
		}
		RulePacGenerator gen = new RulePacGenerator();
		gen.createEventScrubRulePac(filelocation + "rp-00-eventScrub.yaml");
		gen.createHBMonitorRulePac(filelocation + "rp-01-hbMonitor.yaml");
		gen.createHBTORulePac(filelocation + "rp-02hbto.yaml");
	}

	public RulePacGenerator() {
		createActionInstances(null);
	}

	private void createEventScrubRulePac(String filename) {
		YamlRule r0 = new YamlRule("ScrubRP:ScrubStream",
				"clean and augment events and insert them into a new stream called scrubbed",
				"insert into Scrubbed select * from Event(attribute('scrub') != '1')");
		YamlActionInstance ai0 = new YamlActionInstance("AddUpdateSlotWithValue", "Internal:scrubEvent",
				"Removes known E2open Domain names for hostname, add hubname");
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		ai0.addParameter(hostnameSlot, "&e2openDNSShortName'" + hostnameSlot + "';");
		ai0.addParameter("hubname", "&hostname2hub'" + hostnameSlot + "';");
		ai0.addParameter("scrub", "1");
		r0.addSerialAction(ai0);
		r0.addSerialAction(actors.get("Internal:ResendNewEvent"));
		YamlRulePac rp = new YamlRulePac("ScrubRules", "Will scrub and augment events");
		rp.addRule(r0);
		writeRulePac(rp, filename);
	}

	private void createHBMonitorRulePac(String filename) {
		// TODO Auto-generated method stub

	}

	public void createHBTORulePac(String filename) {
		YamlRule r0 = new YamlRule("HBTO:HBTOStream", "insert HBTO events into a new stream called heartbeat",
				"insert into heartbeat select * from Scrubbed(type = 'HBTO')");

		YamlRule r1 = new YamlRule("HBTO:CreateProblem", "Create a problem for HBTO that are not clearing events",
				"select * from heartbeat(type = 'HBTO' and status != Status.CLEARING)");
		r1.addSerialAction(actors.get("Internal:HBTOProblem"));

		YamlRule r2 = new YamlRule("HBTO:CloseClearedProblem", "Closes a problem when it clears",
				"select * from heartbeat(type = 'HBTO' and status = Status.CLEARING)");
		r2.addSerialAction(actors.get("Internal:HBTOCloseProblem"));

		YamlRule r3 = new YamlRule("HBTO:CountEvents", "Print Events per second for HBTO events to stdout",
				"select count(*) as cnt from heartbeat.win:time_batch(1 second)");
		r3.addParallelAction(actors.get("Internal:PrintToStdOut"));

		YamlRulePac rp = new YamlRulePac("HBTORules", "Rules related to the handling of Heartbeat timeouts");
		rp.addRule(r0);
		rp.addRule(r1);
		rp.addRule(r2);
		rp.addRule(r3);
		writeRulePac(rp, filename);
	}

	public void createActionInstances(String filename) {
		try {
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

			YamlActionInstance a8 = new YamlActionInstance("AddUpdateSlotWithValue", "Internal:truncateKnownFQDN",
					"Removes known E2open Domain names for hostname");
			String hostnameSlot = Event.HeaderAttribute.hostname.name();
			a8.addParameter(hostnameSlot, "&e2openDNSShortName'" + hostnameSlot + "';");

			YamlActionInstance a9 = new YamlActionInstance("ResendEvent", "Internal:ResendNewEvent", "Submits only New events");
			a9.addParameter(ResendEvent.CHOICE, ResendEvent.SUBMIT_ACTION.NEW.name());

			actors.put(a1.getName(), a1);
			actors.put(a2.getName(), a2);
			actors.put(a3.getName(), a3);
			actors.put(a4.getName(), a4);
			actors.put(a5.getName(), a5);
			actors.put(a6.getName(), a6);
			actors.put(a7.getName(), a7);
			actors.put(a8.getName(), a8);
			actors.put(a9.getName(), a9);
			if (null != filename) {
				File f = new File(filename);
				Yaml.dump(actors, f);
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private void writeRulePac(YamlRulePac rp, String filename) {
		try {
			File f = new File(filename);
			Yaml.dump(rp, f);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

}
