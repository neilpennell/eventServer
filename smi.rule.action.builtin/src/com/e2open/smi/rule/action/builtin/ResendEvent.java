package com.e2open.smi.rule.action.builtin;

import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;

public class ResendEvent extends Action {
	public static final String CHOICE = "SUBMIT_ACTION";
	private static final long serialVersionUID = -7673798291913361048L;

	public enum SUBMIT_ACTION {
		NEW, OLD, BOTH
	};

	public ResendEvent() {
		setName("ResendEvent");
		setDescription("will submit the event back to be evaluted again");
	}

	/**
	 * @param newEvents
	 * @param oldEvents
	 * @param params key SUBMIT_ACTION defaults to NEW if not set
	 */
	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String, String> params) {
		String request = params.get(CHOICE);
		if(null == request) request = SUBMIT_ACTION.NEW.name();
		SUBMIT_ACTION todo = SUBMIT_ACTION.valueOf(request);
		switch (todo) {
		case NEW:
			send(am, newEvents);
			break;
		case OLD:
			send(am, oldEvents);
			break;
		case BOTH:
			send(am, newEvents);
			send(am, oldEvents);
			break;
		}
	}

	private void send(ActionMgr am, List<Event> eventList) {
		for (Event event : eventList) {
			am.route(event);
		}
	}

}
