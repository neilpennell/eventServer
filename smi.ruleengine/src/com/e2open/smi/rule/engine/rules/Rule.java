package com.e2open.smi.rule.engine.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringEscapeUtils;

import com.e2open.smi.rule.engine.CEPServer;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class Rule {
	private String name, description, criteria;

	private EPStatement eps = null;
	private ArrayList<ActionInstance> actions = new ArrayList<ActionInstance>();
	private Date lastUpdated = new Date();

	public Rule() {
	}

	public Rule(String name, String description, String criteria, ActionInstance[] actionInstances) {
		setName(name);
		setDescription(description);
		setCriteria(criteria);
		for (ActionInstance actionName : actionInstances) {
			addActionInstance(actionName);
		}
		updateTime();
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addActionInstance(ActionInstance a) {
		actions.add(a);
		updateTime();
	}

	public boolean isActive() {
		return eps != null;
	}

	public boolean hasActionInstance(String name) {
		return getActionInstanceNames().contains(name);
	}

	public ActionInstance getActionInstance(String actionName) {
		ActionInstance findMe = null;
		for (ActionInstance action : actions) {
			if (action.getName().equals(actionName)) {
				findMe = action;
				break;
			}
		}
		if (eps == null)
			CEPServer.getLogger().info(actionName + " not found");
		return findMe;
	}

	public List<String> getActionInstanceNames() {
		ArrayList<String> actionNames = new ArrayList<String>();
		for (ActionInstance action : actions) {
			actionNames.add(action.getName());
		}
		return actionNames;
	}

	public ArrayList<ActionInstance> getActionInstances() {
		ArrayList<ActionInstance> aiList = new ArrayList<ActionInstance>(actions);
		return aiList;
	}

	public boolean removeActionInstance(String actionName) {
		ActionInstance removeMe = null;
		for (ActionInstance action : actions) {
			if (action.getName().equals(actionName)) {
				removeMe = action;
				break;
			}
		}
		if (eps != null)
			eps.removeListener(removeMe);
		else
			CEPServer.getLogger().info(actionName + " not found");
		updateTime();
		return actions.remove(removeMe);
	}

	public void removeActionInstances() {
		for (String actionName : getActionInstanceNames()) {
			removeActionInstance(actionName);
		}
		updateTime();
	}

	public void start() {
		if (eps == null || !eps.isStarted()) {
			CEPServer.getLogger().log(Level.CONFIG, "Start rule " + getName());
			EPServiceProvider defaultSvc = EPServiceProviderManager.getDefaultProvider();
			// EPServiceProvider defaultSvc =
			// EPServiceProviderManager.getDefaultProvider(CEPServer.getServer().cfg);
			EPAdministrator admin = defaultSvc.getEPAdministrator();

			eps = admin.createEPL(criteria, name);
			eps.removeAllListeners();
			for (ActionInstance action : actions) {
				eps.addListener(action);
			}
			eps.start();
			updateTime();
		}
	}

	public void stop() {
		if (eps != null) {
			CEPServer.getLogger().log(Level.CONFIG, "Stop rule " + getName());
			eps.stop();
			eps.removeAllListeners();
			eps.destroy();
			updateTime();
			// removeActionInstances();
		}
		eps = null;
	}

	@Override
	public String toString() {
		String line = "";
		line += "name=" + name + ", ";
		line += "active=" + isActive() + ", ";
		line += "description=" + description + ", ";
		line += "criteria=" + criteria + ", ";
		line += "actions=[";
		for (ActionInstance an : actions) {
			line += an.getName() + ", ";
		}
		// remove trailing comma
		line = line.trim().substring(0, line.length() - 2);
		line += "] ";
		return line;
	}

	public Date getLastUpdate() {
		return lastUpdated;
	}

	public String toHTML() {
		String html = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + "<div>" + "<table>";
		html += "<tr>" + "<td>Name</td>" + "<td>" + name + "</td>" + "</tr>";
		html += "<tr>" + "<td>Description</td>" + "<td>" + description + "</td>" + "</tr>";
		html += "<tr>" + "<td>Criteria</td>" + "<td>" + criteria + "</td>" + "</tr>";
		int i = 0;
		for (ActionInstance ai : actions) {
			if (ai instanceof ActionInstanceOrdered) {
				ActionInstanceOrdered aio = (ActionInstanceOrdered)ai;
				for (ActionInstance oai : aio.getActionInstances()) {
					html += "<tr>" + "<td>" + i++ + "</td>" + "<td>" + StringEscapeUtils.escapeHtml(oai.getActionName()) + "</td>"
					+ "</tr>";
				}
			} else
				html += "<tr>" + "<td>" + i++ + "</td>" + "<td>" + StringEscapeUtils.escapeHtml(ai.getActionName()) + "</td>"
						+ "</tr>";
		}
		html += "</table>" + "</div>" + "</div>";
		return html;
	}

	private void updateTime() {
		lastUpdated = new Date();
	}

}
