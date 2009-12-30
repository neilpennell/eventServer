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

import com.e2open.smi.rule.engine.rules.Action;
import com.e2open.smi.rule.engine.rules.ActionMgr;

public class ActionCollectionAdapter extends AbstractEntityCollectionAdapter<Action> {
	private static final String ID_PREFIX = "tag:smi.e2open.com,2009:action:entry:";
	private Factory factory = new Abdera().getFactory();

	@Override
	public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getContent(Action entry, RequestContext request) throws ResponseContextException {
		Content content = factory.newContent(Content.Type.XHTML);
		String text = entry.toHTML();
		content.setValue(text);
		return content;
	}

	@Override
	public Iterable<Action> getEntries(RequestContext request) throws ResponseContextException {
		return ActionMgr.getInstance().getActions();
	}

	@Override
	public Action getEntry(String resourceName, RequestContext request) throws ResponseContextException {
		String id = resourceName.substring(ID_PREFIX.length());
		return ActionMgr.getInstance().getAction(id);
	}

	@Override
	public String getId(Action entry) throws ResponseContextException {
		return ID_PREFIX + entry.getName();
	}

	@Override
	public String getName(Action entry) throws ResponseContextException {
		return entry.getName();
	}

	@Override
	public String getTitle(Action entry) throws ResponseContextException {
		return entry.getDescription();
	}

	@Override
	public Date getUpdated(Action entry) throws ResponseContextException {
		return null;
	}

	@Override
	public Action postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content,
			RequestContext request) throws ResponseContextException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putEntry(Action entry, String title, Date updated, List<Person> authors, String summary, Content content,
			RequestContext request) throws ResponseContextException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthor(RequestContext request) throws ResponseContextException {
		return "E2open SMI";
	}

	@Override
	public String getId(RequestContext request) {
		return "tag:smi.e2open.com,2009:action:feed";
	}

	public String getTitle(RequestContext request) {
		return "Action List";
	}

}
