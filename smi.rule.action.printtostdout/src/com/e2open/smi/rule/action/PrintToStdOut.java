package com.e2open.smi.rule.action;

import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;

public class PrintToStdOut extends Action {
	private static final long serialVersionUID = 5093438847881416715L;

	public PrintToStdOut() {
		setName("PrintToStdOut");
		setDescription("will write to stdout the contents of the event");
	}

	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String,String>params) {
		System.out.println("New Events: " + newEvents.size());
		for (Event event : newEvents) {
			System.out.println(event.toString());
		}
		System.out.println("Old Events: " + oldEvents.size());
		for (Event event : oldEvents) {
			System.out.println(event.toString());
		}
	}

}
