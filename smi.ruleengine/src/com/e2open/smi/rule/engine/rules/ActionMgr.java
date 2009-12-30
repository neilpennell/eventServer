package com.e2open.smi.rule.engine.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.e2open.smi.rule.engine.CEPServer;
import com.e2open.smi.rule.engine.NotRegistered;
import com.e2open.smi.rule.engine.eif.event.Event;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class ActionMgr {
	private ArrayList<Action> actionList = new ArrayList<Action>();
	private static ActionMgr am = new ActionMgr();

	public static ActionMgr getInstance() {
		return am;
	}

	public void route(Event e) {
		EPServiceProvider engine = EPServiceProviderManager.getDefaultProvider();
		EPRuntime runtimeEngine = engine.getEPRuntime();
		runtimeEngine.route(e);
	}

	public void addAction(Action a) {
		CEPServer.getLogger().log(Level.CONFIG, "Action loaded: " + a.getName() + " - " + a.getDescription());
		actionList.add(a);
	}

	public void removeAction(Action a) {
		CEPServer.getLogger().log(Level.CONFIG, "Action removed: " + a.getName() + " - " + a.getDescription());
		actionList.remove(a);
	}

	public Map<String, String> getActionNamesAndDescriptions() {
		HashMap<String, String> cl = new HashMap<String, String>();
		for (Action action : actionList) {
			cl.put(action.getName(), action.getDescription());
		}
		return cl;
	}

	public Action getAction(String name) {
		Action a = null;
		for (Action action : actionList) {
			if (action.getName().equals(name)) {
				a = action;
				break;
			}
		}
		if (null == a)
			a = new NotRegistered(name);
		return a;
	}

	public Iterable<Action> getActions() {
		ArrayList<Action> list = new ArrayList<Action>(actionList);
		return list;
	}
}
