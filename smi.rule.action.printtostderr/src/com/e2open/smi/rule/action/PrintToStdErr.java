package com.e2open.smi.rule.action;

import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;

public class PrintToStdErr extends Action {
	private static final long serialVersionUID = -8126202164912472068L;

	public PrintToStdErr() {
		setName("PrintToStdErr");
		setDescription("will write to stderr the contents of the event");
	}

	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String,String>params) {
		System.err.println("New Events: " + newEvents.size());
		for (Event event : newEvents) {
			System.out.println(event.toString());
		}
		System.err.println("Old Events: " + oldEvents.size());
		for (Event event : oldEvents) {
			System.out.println(event.toString());
		}
	}

}
