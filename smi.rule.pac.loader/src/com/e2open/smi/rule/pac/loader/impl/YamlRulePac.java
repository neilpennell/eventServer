package com.e2open.smi.rule.pac.loader.impl;

import java.io.Serializable;
import java.util.ArrayList;

public class YamlRulePac implements Serializable{
	private static final long serialVersionUID = 3579079801115931425L;
	private String name = "", description = "";
	private ArrayList<YamlRule> rules = new ArrayList<YamlRule>();
	
	public YamlRulePac() {
	}
	
	public YamlRulePac(String name, String description) {
		setName(name);
		setDescription(description);
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
	public ArrayList<YamlRule> getRules() {
		return rules;
	}
	public void setRules(ArrayList<YamlRule> rules) {
		this.rules = rules;
	}
	public void addRule(YamlRule rule){
		this.rules.add(rule);
	}
	
}
