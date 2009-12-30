package com.e2open.smi.rule.engine;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;

public class NotRegistered extends Action {
	private static final long serialVersionUID = 8797633627953946241L;
	private String actionName;
	 public NotRegistered(String an) {
		 actionName = an;
	}
	 
	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String, String> params) {
		CEPServer.getLogger().log(Level.WARNING, actionName+" Not a registered action");
	}

}
