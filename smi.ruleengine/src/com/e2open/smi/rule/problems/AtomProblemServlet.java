package com.e2open.smi.rule.problems;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;



public class AtomProblemServlet extends AbderaServlet {
	private static final long serialVersionUID = 6365822193997172728L;

	@Override
	protected Provider createProvider() {
		ProblemCollectionAdapter ca = new ProblemCollectionAdapter();
		ca.setHref("problem");

		SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
		wi.setTitle("Problem Workspace");
		wi.addCollection(ca);

		DefaultProvider provider = new DefaultProvider("/problems/");
		provider.addWorkspace(wi);

		provider.init(getAbdera(), null);
		return provider;
	}
}
