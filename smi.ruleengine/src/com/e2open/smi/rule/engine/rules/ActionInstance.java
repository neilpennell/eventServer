package com.e2open.smi.rule.engine.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class ActionInstance implements StatementAwareUpdateListener, Serializable {
	private static final long serialVersionUID = 7417431935481507444L;

	private String name = "", description = "", actionName = "";

	public ActionInstance(String actionName) {
		this.actionName = actionName;
	}

	protected ActionInstance() {
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
		Action action = ActionMgr.getInstance().getAction(actionName);
		ArrayList<Event> newEventList = adapterEventBeanArray(newEvents);
		ArrayList<Event> oldEventList = adapterEventBeanArray(oldEvents);
		action.processEvents(ActionMgr.getInstance(), newEventList, oldEventList, namedParam);
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<Event> adapterEventBeanArray(EventBean[] events) {
		ArrayList<Event> eventList = new ArrayList<Event>();
		if (null != events) {
			for (EventBean newEvent : events) {
				EventType et = newEvent.getEventType();
				if (et.getName() != null) {
					Event e = (Event) newEvent.getUnderlying();
					eventList.add(e);
				} else { // must be a map
					Event e = new Event();
					e.setType("INTERNAL");
					e.setSource("MAP");
					Map m = (Map) newEvent.getUnderlying();
					for (Object key : m.keySet()) {
						e.addAttribute(key.toString(), m.get(key).toString());
					}
					eventList.add(e);
				}
			}
		}
		return eventList;
	}

	private HashMap<String, String> namedParam = new HashMap<String, String>();

	public void addParm(String key, String value) {
		namedParam.put(key, value);
	}

	public void removeParm(String name) {
		namedParam.remove(name);
	}

	public String getNamedParm(String key) {
		return namedParam.get(key);
	}

	public HashMap<String, String> getNamedParms() {
		HashMap<String, String> map = new HashMap<String, String>(namedParam);
		return map;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActionName() {
		return actionName;
	}

	@Override
	public String toString() {
		String output = "";
		output += "ActionName=" + actionName + " Name=" + name + " Description=" + description;
		return output;
	}

	public String toHTML() {
		String html = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + "<div>" + "<table>";
		html += "<tr>" + "<td>Name</td>" + "<td>" + name + "</td>" + "</tr>";
		html += "<tr>" + "<td>Description</td>" + "<td>" + description + "</td>" + "</tr>";
		html += "<tr>" + "<td>Action</td>" + "<td>" + actionName + "</td>" + "</tr>";
		for (String key : namedParam.keySet()) {
			html += "<tr>" + "<td>" + key + "</td>" + "<td>" + StringEscapeUtils.escapeHtml(namedParam.get(key)) + "</td>"
					+ "</tr>";
		}
		html += "</table>" + "</div>" + "</div>";
		return html;
	}
}
