/**
 * 
 */
package com.e2open.smi.rule.engine.eif.event;

public class Slot{
	private String name;
	private String value;
	public Slot(String name, String value) {
		this.name = name; this.value = value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}