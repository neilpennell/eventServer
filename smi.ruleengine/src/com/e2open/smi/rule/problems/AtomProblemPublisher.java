package com.e2open.smi.rule.problems;

import org.apache.abdera.protocol.server.servlet.AbderaServlet;

import com.e2open.smi.rule.atom.AtomFeedPublisher;

public class AtomProblemPublisher extends AtomFeedPublisher {
	private AtomProblemServlet servlet = new AtomProblemServlet();
	
	@Override
	public String getAlias() {
		return "/problems";
	}

	@Override
	public String getName() {
		return "Atom_Feed_for_Problems";
	}

	@Override
	public AbderaServlet getServlet() {
		return servlet;
	}

}
