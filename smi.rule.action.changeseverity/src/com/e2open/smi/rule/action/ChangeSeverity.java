package com.e2open.smi.rule.action;

import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.eif.event.Severity;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;
import com.e2open.smi.rule.engine.rules.misc.Utilities;

public class ChangeSeverity extends Action {
	public static final String SEVERITY = "SEVERITY";
	private static final long serialVersionUID = -908388996734850785L;

	public ChangeSeverity() {
		setName("ChangeSeverity");
		setDescription("Will change the Events severity to what is specified in key SEVERITY");
		addParam(SEVERITY, "new value that should be placed in severity");
	}

	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String,String>params) {
		for (Event event : newEvents) {
			String slotValue = Utilities.parse(params.get(SEVERITY), event);
			event.setSeverity(Severity.valueOf(slotValue));
//			am.route(event);
		}
	}

}
