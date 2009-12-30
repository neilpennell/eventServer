package com.e2open.smi.rule.pac;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.e2open.smi.rule.engine.eif.event.Status;
import com.e2open.smi.rule.engine.rules.ActionInstance;
import com.e2open.smi.rule.engine.rules.ActionInstanceOrdered;
import com.e2open.smi.rule.engine.rules.Rule;
import com.e2open.smi.rule.engine.rules.RulePacDefinition;
import com.e2open.smi.rule.problems.Problem;

public class TestRulePac extends RulePacDefinition {
	private static final long serialVersionUID = 7373924673882301156L;
	private ArrayList<Rule> rules = new ArrayList<Rule>();

	public TestRulePac() {
		// rule.0.name=dumpWarnEvents
		// rule.0.description=Display all Warning events to stdout
		// rule.0.criteria=select * from Event where severity = Severity.WARNING
		// rule.0.action.0=PrintToStdOut
		ActionInstance printToStdOut = new ActionInstance("PrintToStdOut");
		printToStdOut.setName(getName() + ":" + "PrintToStdOut");
		printToStdOut.setDescription("PrintToStdOut desc");
		rules.add(new Rule(getName() + ":" + "dumpWarnEvents"+new Date().getTime(), "Display all Warning events to stdout",
				"select * from Event where severity = Severity.WARNING", new ActionInstance[] { printToStdOut }));

//		rules.add(new Rule(getName() + ":" + "countEvents"+new Date().getTime(), "events per sec to stdout",
//				"select count(*) as cnt from Event.win:time_batch(1 second)", new ActionInstance[] { printToStdOut }));
		
		rules.add(new Rule(getName() + ":" + "testSlot"+new Date().getTime(), "when the slot abc exist",
				"select * from Event where exists(Event.slot('abc'))", new ActionInstance[] { printToStdOut }));
		
		ActionInstanceOrdered orderedActions = new ActionInstanceOrdered();
		ActionInstance ai1 = new ActionInstance("AddUpdateSlotWithValue");
		ai1.setName(getName() + ":slot1");
		ai1.addParm("severity", "FATAL");
		orderedActions.addActionInstance(ai1);
		
		orderedActions.addActionInstance(printToStdOut);

		ActionInstance ai2 = new ActionInstance("AddUpdateSlotWithValue");
		ai2.setName(getName() + ":slot2");
		ai2.addParm("slot1", "set");
		orderedActions.addActionInstance(ai2);

		orderedActions.addActionInstance(printToStdOut);

		rules.add(new Rule(getName() + ":" + "testActionChaining", "Will add slot1, print, add slot2, print",
				"select * from Event where severity = Severity.MINOR", new ActionInstance[] { orderedActions }));

		
		ActionInstance ai3 = new ActionInstance("AddUpdateProblem");
		ai3.setName(getName() + ":HBTOProblem");
		ai3.setDescription("Create a problem for HBTO that are not clearing events");
		
		ai3.addParm(Problem.key.CORRELATOR.name(), "&slot.severity; &slot.type; &slot.hostname;");
		ai3.addParm(Problem.key.TITLE.name(), "HBTO for host=&slot.hostname;");
		ai3.addParm(Problem.key.OWNER.name(), "system");
		ai3.addParm(Problem.key.STATUS.name(), Status.RESPONSE.name());
		ai3.addParm("myAttribute", "value for my attribute &slot.severity; &formattedCurrentDateTime;");

		rules.add(new Rule(getName() + ":" + "testHBTO-ProblemCreation", "Will create a problem for HBTO",
				"select * from Event(type = 'HBTO' and status != Status.CLEARING) ", new ActionInstance[] { ai3 }));

		///////
		ActionInstance ai4 = new ActionInstance("AddUpdateProblem");
		ai4.setName(getName() + ":HBTOCloseProblem");
		ai4.setDescription("Close a Problem when a clearing event arrives");

		ai4.addParm(Problem.key.CORRELATOR.name(), "&slot.severity; &slot.type; &slot.hostname;");
		ai4.addParm(Problem.key.OWNER.name(), "&slot.owner;");
		ai4.addParm(Problem.key.STATUS.name(), Status.CLOSED.name());

		rules.add(new Rule(getName() + ":" + "testHBTO-ProblemClose", "Will close a problem for HBTO",
				"select * from Event(type = 'HBTO'and status = Status.CLEARING)", new ActionInstance[] { ai4 }));

		
	}

	
	@Override
	public String getDescription() {
		return "This is a test of general functionality";
	}

	@Override
	public String getName() {
		return "TestRulePac";
	}

	@Override
	public List<Rule> getRules() {

		//
		// rule.1.name=dumpFatalEvents
		// rule.1.description=Display all Fatal events to stdout and stderr
		// rule.1.criteria=select * from Event where severity = Severity.FATAL
		// rule.1.action.0=PrintToStdOut
		// rule.1.action.1=PrintToStdErr
		//
		// rule.2.name=dumpCriticalEvents
		// rule.2.description=Display all Critical events to stderr
		// rule.2.criteria=select * from Event where severity =
		// Severity.CRITICAL
		// rule.2.action.0=PrintToStdErr
		//
		// rule.3.name=dumpMEEvents
		// rule.3.description=Display events when the Type/class is ME to stdout
		// rule.3.criteria=select * from Event(type = 'ME')
		// rule.3.action.0=PrintToStdOut
		//
		// rule.4.name=increaseSeverityToFatal
		// rule.4.description=Raise the severity of all MINOR events to FATAL
		// rule.4.criteria=select * from Event(severity = Severity.MINOR)
		// rule.4.action.0=ChangeSeverity
		// rule.4.action.0.param.0=FATAL
		//
		// rule.5.name=addSlotToEvent
		// rule.5.description=Add a new slot (newSlot)
		// rule.5.criteria=select * from Event(severity = Severity.MINOR)
		// rule.5.action.0=AddUpdateSlotWithValue
		// rule.5.action.0.param.0=newSlot
		// rule.5.action.0.param.1=slot contain the value of
		// severity=&slot.severity;
		//
		// rule.6.name=addSlotToEvent2
		// rule.6.description=change the message slot for 2 slots
		// rule.6.criteria=select * from Event(type='CLI')
		// rule.6.action.0=AddUpdateSlotWithValue
		// rule.6.action.0.param.0=msg
		// rule.6.action.0.param.1=&formattedCurrentDateTime; - slot contain the
		// value of severity=&slot.severity;
		// rule.6.action.0.param.2=TYPE
		// rule.6.action.0.param.3=ME

		return rules;
	}

}
