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

import com.e2open.smi.rule.engine.rules.ActionInstance;
import com.e2open.smi.rule.engine.rules.RuleManager;

public class ActionInstanceCollectionAdapter extends AbstractEntityCollectionAdapter<ActionInstance> {
	private static final String ID_PREFIX = "tag:smi.e2open.com,2009:actioninstance:entry:";
	private Factory factory = new Abdera().getFactory();

	@Override
	public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public Object getContent(ActionInstance entry, RequestContext request) throws ResponseContextException {
		Content content = factory.newContent(Content.Type.XHTML);
		String text = entry.toHTML();
		content.setValue(text);
		return content;
	}

	@Override
	public Iterable<ActionInstance> getEntries(RequestContext request) throws ResponseContextException {
		return RuleManager.getInstance().getRegisteredActionInstances();
	}

	@Override
	public ActionInstance getEntry(String resourceName, RequestContext request) throws ResponseContextException {
		String id = resourceName.substring(ID_PREFIX.length());
		return RuleManager.getInstance().getRegisteredActionInstancesMap().get(id);
	}

	@Override
	public String getId(ActionInstance entry) throws ResponseContextException {
		return ID_PREFIX + entry.getName();
	}

	@Override
	public String getName(ActionInstance entry) throws ResponseContextException {
		return entry.getName();
	}

	@Override
	public String getTitle(ActionInstance entry) throws ResponseContextException {
		return entry.getDescription();
	}

	@Override
	public Date getUpdated(ActionInstance entry) throws ResponseContextException {
		return null;
	}

	@Override
	public ActionInstance postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public void putEntry(ActionInstance entry, String title, Date updated, List<Person> authors, String summary, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public String getAuthor(RequestContext request) throws ResponseContextException {
		return "E2open SMI";
	}

	@Override
	public String getId(RequestContext request) {
		return "tag:smi.e2open.com,2009:actioninstance:feed";
	}

	public String getTitle(RequestContext request) {
		return "ActionInstance List";
	}

}
