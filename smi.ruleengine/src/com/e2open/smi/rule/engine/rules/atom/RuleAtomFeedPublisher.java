package com.e2open.smi.rule.engine.rules.atom;

import org.apache.abdera.protocol.server.servlet.AbderaServlet;

import com.e2open.smi.rule.atom.AtomFeedPublisher;

public class RuleAtomFeedPublisher extends AtomFeedPublisher {
	private RuleAtomServlet servlet = new RuleAtomServlet();
	
	@Override
	public String getAlias() {
		return "/rules";
	}

	@Override
	public String getName() {
		return "Atom_Feed_for_Rules";
	}

	@Override
	public AbderaServlet getServlet() {
		return servlet;
	}

}
