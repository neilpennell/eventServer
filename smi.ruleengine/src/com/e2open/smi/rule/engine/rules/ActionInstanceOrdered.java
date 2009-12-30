package com.e2open.smi.rule.engine.rules;

import java.util.ArrayList;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

public class ActionInstanceOrdered extends ActionInstance {
	private static final long serialVersionUID = 3882169217810611951L;
	private ArrayList<ActionInstance> actionInstances = new ArrayList<ActionInstance>();

	protected ActionInstanceOrdered(String name) {
	}

	public ActionInstanceOrdered() {
		super("ActionInstanceOrdered");
	}

	@Override
	public String getName() {
		return "ActionInstanceOrdered";
	}

	@Override
	public String getDescription() {
		return "This will execute the ActionInstances in the order of being added.  Does not use parameters";
	}

	public void addActionInstance(ActionInstance ai) {
		actionInstances.add(ai);
	}

	public ArrayList<ActionInstance> getActionInstances() {
		return actionInstances;
	}

	public void removeActionInstance(ActionInstance ai) {
		actionInstances.remove(ai);
	}

	/**
	 * @param newEvents
	 *            - is any new events. This will be null or empty if the update
	 *            is for old events only.
	 * @param oldEvents
	 *            - is any old events. This will be null or empty if the update
	 *            is for new events only.
	 * @param statement
	 *            - is the statement producing the result
	 * @param epServiceProvider
	 *            - is the engine instance that provided the administrative API
	 *            that created the statement which produces the result
	 */
	public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPServiceProvider epServiceProvider) {
		ArrayList<Event> newEventList = adapterEventBeanArray(newEvents);
		ArrayList<Event> oldEventList = adapterEventBeanArray(oldEvents);
		for (ActionInstance ai : actionInstances) {
			String actionName = ai.getActionName();
			Action action = ActionMgr.getInstance().getAction(actionName);
			action.processEvents(ActionMgr.getInstance(), newEventList, oldEventList, ai.getNamedParms());
		}
	}
}
