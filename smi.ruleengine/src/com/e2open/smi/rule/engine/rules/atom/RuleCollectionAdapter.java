package com.e2open.smi.rule.engine.rules.atom;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;

import com.e2open.smi.rule.engine.rules.Rule;
import com.e2open.smi.rule.engine.rules.RuleManager;

public class RuleCollectionAdapter extends AbstractEntityCollectionAdapter<Rule> {
	private static final String ID_PREFIX = "tag:smi.e2open.com,2009:rule:entry:";
	private Factory factory = new Abdera().getFactory();

	@Override
	public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public Object getContent(Rule entry, RequestContext request) throws ResponseContextException {
		Content content = factory.newContent(Content.Type.XHTML);
		String text = entry.toHTML();
		content.setValue(text);
		return content;
	}

	@Override
	public Iterable<Rule> getEntries(RequestContext request) throws ResponseContextException {
		return RuleManager.getInstance().getRulesMap().values();
	}

	@Override
	public Rule getEntry(String resourceName, RequestContext request) throws ResponseContextException {
		String id = resourceName.substring(ID_PREFIX.length());
		return RuleManager.getInstance().getRule(id);
	}

	@Override
	public String getId(Rule entry) throws ResponseContextException {
		return ID_PREFIX + entry.getName();
	}

	@Override
	public String getName(Rule entry) throws ResponseContextException {
		return entry.getName();
	}

	@Override
	public String getTitle(Rule entry) throws ResponseContextException {
		return entry.getDescription();
	}

	@Override
	public Date getUpdated(Rule entry) throws ResponseContextException {
		return entry.getLastUpdate();
	}

	@Override
	public Rule postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public void putEntry(Rule entry, String title, Date updated, List<Person> authors, String summary, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public String getAuthor(RequestContext request) throws ResponseContextException {
		return "E2open SMI";
	}

	@Override
	public String getId(RequestContext request) {
		return "tag:smi.e2open.com,2009:rule:feed";
	}

	public String getTitle(RequestContext request) {
		return "Rule List";
	}
}
