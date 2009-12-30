package com.e2open.smi.rule.action;

import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;
import com.e2open.smi.rule.engine.rules.misc.Utilities;

public class AddUpdateSlotWithValue extends Action {
	private static final long serialVersionUID = 6143785978929749864L;

	public AddUpdateSlotWithValue() {
		setName("AddUpdateSlotWithValue");
		setDescription("Will add/update slot (key) with value/expression (value)");
	}

	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String, String> params) {
		for (Event e : newEvents) {
			Event event = e;
			for (String slotName : params.keySet()) {
				String slotValue = Utilities.parse(params.get(slotName), event);
				event.addSlot(slotName, slotValue);
			}
//			am.route(event);
		}
	}
}
