package com.e2open.smi.rule.engine.rules;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.e2open.smi.rule.engine.CEPServer;

/**
 * due to bug 6710498
 * (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6710498) all listX have
 * been changed from returning array's to other generics
 * 
 * @author npennell
 */
public class RuleManager {
	private static RuleManager ruleManager = new RuleManager();
	private static RuleManagerMBean mbean = null;

	// TODO: still need actions Map in RuleManager
	private HashMap<String, ActionInstance> actions = new HashMap<String, ActionInstance>();
	private HashMap<String, Rule> rules = new HashMap<String, Rule>();

	static public RuleManager getInstance() {
		if (mbean == null) {
			try {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				mbean = new RuleManagerMBean(ruleManager);
				mbs.registerMBean(mbean, new ObjectName("eif.server:type=RuleManager"));
			} catch (InstanceAlreadyExistsException e) {
				CEPServer.getLogger().warning("InstanceAlreadyExistsException: " + e.getMessage());
			} catch (MBeanRegistrationException e) {
				CEPServer.getLogger().warning("MBeanRegistrationException: " + e.getMessage());
			} catch (NotCompliantMBeanException e) {
				CEPServer.getLogger().warning("NotCompliantMBeanException: " + e.getMessage());
			} catch (MalformedObjectNameException e) {
				CEPServer.getLogger().warning("MalformedObjectNameException: " + e.getMessage());
			} catch (NullPointerException e) {
				CEPServer.getLogger().warning("NullPointerException: " + e.getMessage());
			}
		}
		return ruleManager;
	}

	private HashMap<String, ArrayList<Rule>> actionAssociationRequests = new HashMap<String, ArrayList<Rule>>();

	public void updateRuleWhenActionAvailable(Rule rule, String actionName) {
		if (actionAssociationRequests.containsKey(rule))
			actionAssociationRequests.get(actionName).add(rule);
		else {
			ArrayList<Rule> list = new ArrayList<Rule>();
			list.add(rule);
			actionAssociationRequests.put(actionName, list);
		}
	}

	public void saveRules(String location) {
		try {
			Properties rulesToBePersisted = new Properties();
			int i = 0;
			for (String ruleName : rules.keySet()) {
				String base = "rule." + i;
				rulesToBePersisted.put(base + ".name", ruleName);
				rulesToBePersisted.put(base + ".descriptin", rules.get(ruleName).getDescription());
				rulesToBePersisted.put(base + ".criteria", rules.get(ruleName).getCriteria());
				int j = 0;
				for (String actionName : rules.get(ruleName).getActionInstanceNames()) {
					rulesToBePersisted.put(base + ".action." + j, actionName);
					j++;
				}
				i++;
			}
			String comments = "saved on " + new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
			OutputStream out;
			out = new FileOutputStream(location);
			rulesToBePersisted.storeToXML(out, comments);
			CEPServer.getLogger().info("saved rules to " + location);
		} catch (FileNotFoundException e) {
			CEPServer.getLogger().warning("FileNotFoundException :" + e.getMessage());
		} catch (IOException e) {
			CEPServer.getLogger().warning("IOException :" + e.getMessage());
		}
	}

	public void stopRule(String ruleName) {
		if (rules.containsKey(ruleName)) {
			Rule rule = rules.get(ruleName);
			rule.stop();
			CEPServer.getLogger().info("Stop rule: " + rule.toString());
		} else
			CEPServer.getLogger().info(ruleName + " not found");
	}

	public void startRule(String ruleName) {
		if (rules.containsKey(ruleName)) {
			Rule rule = rules.get(ruleName);
			rule.start();
			CEPServer.getLogger().info("Started rule: " + rule.toString());
		} else
			CEPServer.getLogger().info(ruleName + " not found");
	}

	public void addRuleAction(String ruleName, String actionName) throws AlreadyExistsException {
		Rule rule = rules.get(ruleName);
		if (rule.hasActionInstance(actionName))
			throw new AlreadyExistsException("action:" + actionName + " already associated with rule:" + ruleName);
		ActionInstance action = ruleManager.actions.get(actionName);
		rule.addActionInstance(action);
		CEPServer.getLogger().info("added action " + actionName + " to rule: " + rule.toString());
	}

	public void removeRuleAction(String ruleName, String actionName) {
		Rule rule = rules.get(ruleName);
		if (null != rule) {
			rule.removeActionInstance(actionName);
			CEPServer.getLogger().info("removed action " + actionName + " from rule: " + rule.toString());
		} else
			CEPServer.getLogger().info(actionName + " not found");
	}

	public Map<String, String> listAction() {
		return ActionMgr.getInstance().getActionNamesAndDescriptions();
	}

	public HashMap<String, HashMap<String, Object>> listRules() {
		HashMap<String, HashMap<String, Object>> rl = new HashMap<String, HashMap<String, Object>>();

		for (String rn : rules.keySet()) {
			Rule rule = rules.get(rn);
			HashMap<String, Object> ri = new HashMap<String, Object>();
			ri.put("description", rule.getDescription());
			ri.put("criteria", rule.getCriteria());
			ri.put("active", rule.isActive());
			ArrayList<String> actions = new ArrayList<String>();
			for (String name : rule.getActionInstanceNames()) {
				actions.add(name);
			}
			ri.put("actions", actions);
			rl.put(rule.getName(), ri);
		}
		return rl;
	}

	public void start() {
		for (String key : rules.keySet()) {
			Rule rule = rules.get(key);
			rule.start();
		}
	}

	public class AlreadyExistsException extends Exception {
		private static final long serialVersionUID = 2664704448547505993L;

		public AlreadyExistsException(String ruleName) {
			super(ruleName);
		}
	}

	public void addRule(Rule rule) {
		rules.put(rule.getName(), rule);
		for (ActionInstance ai : rule.getActionInstances()) {
			actions.put(ai.getName(), ai);
			if (ai instanceof ActionInstanceOrdered) {
				ActionInstanceOrdered aio = (ActionInstanceOrdered) ai;
				for (ActionInstance internalAI : aio.getActionInstances()) {
					actions.put(internalAI.getName(), internalAI);
				}
			}
		}
		startRule(rule.getName());
	}

	private static final Integer ZERO = Integer.valueOf(0);

	public void removeRule(String ruleName) {
		stopRule(ruleName);
		// Rule rule = rules.get(ruleName);
		// rule.removeActionInstances();
		HashMap<String, Integer> aiCount = new HashMap<String, Integer>();
		for (Rule rule : rules.values()) {
			for (ActionInstance ai : rule.getActionInstances()) {
				if (ai instanceof ActionInstanceOrdered)
					for (ActionInstance ai2 : ((ActionInstanceOrdered) ai).getActionInstances()) {
						Integer i = aiCount.get(ai2.getName());
						if (null == i) {
							i = ZERO;
							aiCount.put(ai2.getName(), i);
						}
						int j = i.intValue();
						aiCount.put(ai2.getName(), Integer.valueOf(++j));
					}
				Integer i = aiCount.get(ai.getName());
				if (null == i) {
					i = ZERO;
					aiCount.put(ai.getName(), i);
				}
				int j = i.intValue();
				aiCount.put(ai.getName(), Integer.valueOf(++j));
			}
		}
		for (ActionInstance ai : rules.get(ruleName).getActionInstances()) {
			if (ai instanceof ActionInstanceOrdered)
				for (ActionInstance ai2 : ((ActionInstanceOrdered) ai).getActionInstances()) {
					Integer value = aiCount.get(ai2.getName());
					aiCount.put(ai2.getName(), --value);
				}
			Integer value = aiCount.get(ai.getName());
			aiCount.put(ai.getName(), --value);
		}

		for (String key : aiCount.keySet()) {
			if (aiCount.get(key).intValue() == 0)
				actions.remove(key);
		}
		rules.remove(ruleName);
	}

	public void removeRulePac(RulePacDefinition rulePac) {
		CEPServer.getLogger().log(Level.INFO, "Stopping RulePac " + rulePac.getName() + " - " + rulePac.getDescription());
		for (Rule rule : rulePac.getRules()) {
			removeRule(rule.getName());
		}
	}

	public void addRulePac(RulePacDefinition rulePac) {
		CEPServer.getLogger().log(Level.INFO, "starting RulePac " + rulePac.getName() + " - " + rulePac.getDescription());
		for (Rule rule : rulePac.getRules()) {
			addRule(rule);
		}
	}

	public ArrayList<ActionInstance> getRegisteredActionInstances() {
		ArrayList<ActionInstance> aiList = new ArrayList<ActionInstance>(actions.values());
		return aiList;
	}

	public HashMap<String, ActionInstance> getRegisteredActionInstancesMap() {
		HashMap<String, ActionInstance> map = new HashMap<String, ActionInstance>(actions);
		return map;
	}

	public HashMap<String, Rule> getRulesMap() {
		HashMap<String, Rule> map = new HashMap<String, Rule>(rules);
		return map;
	}

	public Rule getRule(String name) {
		return rules.get(name);
	}
}
