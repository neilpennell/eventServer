package com.e2open.smi.rule.action.addupdateproblem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;
import com.e2open.smi.rule.engine.rules.misc.Utilities;
import com.e2open.smi.rule.problems.Problem;
import com.e2open.smi.rule.problems.ProblemManager;

public class AddUpdateProblem extends Action {
	private static final long serialVersionUID = 4089907200470750457L;

	public AddUpdateProblem() {
		setName("AddUpdateProblem");
		setDescription("will create or update a problem based on problem id");
		addParam(Problem.key.CORRELATOR.name(), "Unique ID of how this problem will be identified");
		addParam(Problem.key.TITLE.name(), "The Title of the problem");
		addParam(Problem.key.OWNER.name(), "represents who is currently responsible for the problem");
		addParam(Problem.key.STATUS.name(), "current status of the problem");
		// a problem can have user-defined parameters
	}

	/**
	 * Will not resubmit the event for evaluation again param list contains what
	 * information will be combined to be the correlator
	 */
	@Override
	public void processEvents(ActionMgr am, List<Event> newEvents, List<Event> oldEvents, Map<String, String> params) {
		String rawCorrelator = "";
		String hashedCorrelator = "";

		for (Event event : newEvents) {
			HashMap<String, String> properties = new HashMap<String, String>();
			for (String slotName : params.keySet()) {
				String slotValue = Utilities.parse(params.get(slotName), event);
				properties.put(slotName, slotValue);
			}
			rawCorrelator = properties.get(Problem.key.CORRELATOR.name());
			hashedCorrelator = Utilities.sha1(rawCorrelator);

			Problem p = ProblemManager.getInstance().createUpdateProblem(hashedCorrelator);
			for (String key : properties.keySet()) {
				p.addAttribute(key, properties.get(key));
			}
		}
	}
}
