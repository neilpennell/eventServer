package com.e2open.smi.rule.pac.loader;

import java.util.ArrayList;
import java.util.List;

import com.e2open.smi.rule.engine.rules.ActionInstance;
import com.e2open.smi.rule.engine.rules.ActionInstanceOrdered;
import com.e2open.smi.rule.engine.rules.Rule;
import com.e2open.smi.rule.engine.rules.RulePacDefinition;
import com.e2open.smi.rule.pac.loader.impl.YamlActionInstance;
import com.e2open.smi.rule.pac.loader.impl.YamlRule;
import com.e2open.smi.rule.pac.loader.impl.YamlRulePac;

public class RulePacDefWrapper extends RulePacDefinition {
	private static final long serialVersionUID = 6262166808535713805L;
	private YamlRulePac rp;

	@SuppressWarnings("unused")
	private RulePacDefWrapper() {
	}

	public RulePacDefWrapper(YamlRulePac rp) {
		this.rp = rp;
	}

	@Override
	public String getDescription() {
		return rp.getDescription();
	}

	@Override
	public String getName() {
		return rp.getName();
	}

	@Override
	public List<Rule> getRules() {
		ArrayList<Rule> rules = new ArrayList<Rule>();
		for (YamlRule yr : rp.getRules()) {
			ArrayList<ActionInstance> aiList = new ArrayList<ActionInstance>();

			for (YamlActionInstance yai : yr.getParallelActions()) {
				ActionInstance ai = new ActionInstance(yai.getActionDefinition());
				ai.setName(yai.getName());
				ai.setDescription(yai.getDescription());
				for (String key : yai.getParameters().keySet()) {
					String param = (String)yai.getParameter(key);
					ai.addParm(key, param);
				}
				aiList.add(ai);
			}
			if (yr.getSerialActions().size() > 0) {
				ActionInstanceOrdered aio = new ActionInstanceOrdered();
				for (YamlActionInstance yai : yr.getSerialActions()) {
					ActionInstance ai = new ActionInstance(yai.getActionDefinition());
					ai.setDescription(yai.getDescription());
					ai.setName(yai.getName());
					for (String key : yai.getParameters().keySet()) {
						ai.addParm(key, yai.getParameter(key));
					}
					aio.addActionInstance(ai);
				}
				aiList.add(aio);
			}
			ActionInstance[] aiArray = new ActionInstance[aiList.size()];
			Rule rule = new Rule(yr.getName(), yr.getDescription(), yr.getCriteria(), aiList.toArray(aiArray));
			rules.add(rule);
		}

		return rules;
	}

}
