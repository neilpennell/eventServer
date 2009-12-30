package com.e2open.smi.rule.pac.loader.impl;

import java.io.Serializable;
import java.util.ArrayList;

public class YamlRule implements Serializable {
	private static final long serialVersionUID = 7397765791024465330L;
	private String name="", description="", criteria="";
	private ArrayList<YamlActionInstance> serialActions = new ArrayList<YamlActionInstance>();
	private ArrayList<YamlActionInstance> parallelActions = new ArrayList<YamlActionInstance>();
	
	public YamlRule() {
	}
	
	public YamlRule(String name, String description, String criteria) {
		setName(name);
		setDescription(description);
		setCriteria(criteria);
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
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	public ArrayList<YamlActionInstance> getSerialActions() {
		return serialActions;
	}
	public void setSerialActions(ArrayList<YamlActionInstance> serialActions) {
		this.serialActions = serialActions;
	}
	public ArrayList<YamlActionInstance> getParallelActions() {
		return parallelActions;
	}
	public void setParallelActions(ArrayList<YamlActionInstance> parallelActions) {
		this.parallelActions = parallelActions;
	}
	
	public void addSerialAction(YamlActionInstance a){
		this.serialActions.add(a);
	}
	public void addParallelAction(YamlActionInstance a){
		this.parallelActions.add(a);
	}
}
