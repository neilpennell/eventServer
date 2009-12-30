package com.e2open.smi.rule.pac.loader.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class YamlActionInstance implements Serializable {
	private static final long serialVersionUID = -3557872472468802578L;
	private String name = "", description = "", actionDefinition="";
	private LinkedHashMap<String, String> parameters =new LinkedHashMap<String, String>();
	
	public YamlActionInstance() {
		// TODO Auto-generated constructor stub
	}
	
	public YamlActionInstance(String actionDefinitionName, String name, String description) {
		setName(name);
		setDescription(description);
		setActionDefinition(actionDefinitionName);
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getActionDefinition() {
		return actionDefinition;
	}
	public void setActionDefinition(String actionDefinition) {
		this.actionDefinition = actionDefinition;
	}
	public LinkedHashMap<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(LinkedHashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String name, String value){
		this.parameters.put(name, value);
	}
	
	public String getParameter(String name){
		return this.parameters.get(name);
	}
}
