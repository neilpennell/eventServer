package com.e2open.smi.rule.engine.rules;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.e2open.smi.rule.engine.eif.event.Event;

public abstract class Action  implements Serializable {
	private static final long serialVersionUID = -3521798833495630337L;
	private String name;
	private String description;
	private HashMap<String, String> params = new HashMap<String, String>();

	public abstract void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String, String> params);

	protected void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, String> getParams() {
		return params;
	}

	protected void addParam(String name, String description) {
		params.put(name, description);
	}

	public String toHTML() {
		String html = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + "<div>" + "<table>";
			html += "<tr>" + "<td>Name</td>" + "<td>" + name + "</td>" + "</tr>";
			html += "<tr>" + "<td>Description</td>" + "<td>" + description + "</td>" + "</tr>";
		for (String key : getParams().keySet()) {
			html += "<tr>" + "<td>" + key + "</td>" + "<td>" + StringEscapeUtils.escapeHtml(params.get(key)) + "</td>" + "</tr>";
		}
		html += "</table>" + "</div>" + "</div>";
		return html;
	}
}
