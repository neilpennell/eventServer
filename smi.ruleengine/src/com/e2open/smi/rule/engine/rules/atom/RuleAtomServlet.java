package com.e2open.smi.rule.engine.rules.atom;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;

public class RuleAtomServlet extends AbderaServlet {
	private static final long serialVersionUID = 2295142632534598034L;
	
	@Override
	protected Provider createProvider() {
		ActionCollectionAdapter aca = new ActionCollectionAdapter();
		ActionInstanceCollectionAdapter aica = new ActionInstanceCollectionAdapter();
		RuleCollectionAdapter rca = new RuleCollectionAdapter();
		aca.setHref("action");
		aica.setHref("actor");
		rca.setHref("rule");
		
		SimpleWorkspaceInfo wi = new SimpleWorkspaceInfo();
		wi.setTitle("Rule Workspace");
		wi.addCollection(aca);
		wi.addCollection(aica);
		wi.addCollection(rca);
		
		DefaultProvider provider = new DefaultProvider("/rules/");
		provider.addWorkspace(wi);

		provider.init(getAbdera(), null);
		return provider;
	}

}
