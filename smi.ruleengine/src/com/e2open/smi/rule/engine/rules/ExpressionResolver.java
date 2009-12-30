/**
 * 
 */
package com.e2open.smi.rule.engine.rules;

import com.e2open.smi.rule.engine.eif.event.Event;

public interface ExpressionResolver {
	public String getRegEx();
	public String resolve(String expression, Event e);
	public String getName();
	public String getDescription();
}