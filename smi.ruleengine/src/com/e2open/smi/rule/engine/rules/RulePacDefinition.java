package com.e2open.smi.rule.engine.rules;

import java.io.Serializable;
import java.util.List;

public abstract class RulePacDefinition implements Serializable {
	private static final long serialVersionUID = -6949714509024562643L;
	public abstract String getName();
	public abstract String getDescription();
	public abstract List<Rule> getRules(); 
}
