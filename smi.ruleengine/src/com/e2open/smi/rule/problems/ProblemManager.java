package com.e2open.smi.rule.problems;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.e2open.smi.rule.engine.CEPServer;
import com.e2open.smi.rule.engine.eif.event.Status;

public class ProblemManager {
	static private ProblemManagerMBean mbean;
	static private ProblemManager pm = new ProblemManager();

	private HashMap<String, Problem> activeProblems = new HashMap<String, Problem>();
	private ArrayList<Problem> closedProblems = new ArrayList<Problem>();
	
	static public ProblemManager getInstance() {
		if (mbean == null) {
			try {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				mbean = new ProblemManagerMBean(pm);
				mbs.registerMBean(mbean, new ObjectName("eif.server:type=ProblemManager"));
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

		return pm;
	}

	public void init() {}
	
	public Problem createUpdateProblem(String correlator){
		Problem p = getProblem(correlator);
		if(!problemExists(correlator))
			addProblem(p);
		return p;
	}
	
	public void closeProblem(String correlator){
		Problem p = getProblem(correlator);
		if(null != p){
			p.setStatus(Status.CLOSED);
			removeProblem(correlator);
			closedProblems.add(p);
		}
	}
	
	public void addProblem(Problem p) {
		activeProblems.put(p.getCorrelator(), p);
	}

	public Problem getProblem(String correlator) {
		Problem p = activeProblems.get(correlator);
		if (null == p)
			p = new Problem(correlator);
		return p;
	}
	
	public boolean problemExists(String correlator){
		return activeProblems.containsKey(correlator);
	}

	public void removeProblem(String correlator) {
		activeProblems.remove(correlator);
	}

	public List<Problem> getOpenProblems() {
		ArrayList<Problem> problemList = new ArrayList<Problem>(activeProblems.values());
		return problemList;
	}
	public List<Problem> getClosedProblems() {
		ArrayList<Problem> problemList = new ArrayList<Problem>(closedProblems);
		return problemList;
	}
}
