package com.e2open.smi.rule.problems;

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

public class ProblemCollectionAdapter extends AbstractEntityCollectionAdapter<Problem> {
	 private static final String ID_PREFIX = "tag:smi.e2open.com,2009:problem:entry:";
	private Factory factory = new Abdera().getFactory();
	
	@Override
	public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public Object getContent(Problem entry, RequestContext request) throws ResponseContextException {
		Content content = factory.newContent(Content.Type.XHTML);
		String text = entry.toHTML();
	    content.setValue(text);
	    return content;
	}

	@Override
	public Iterable<Problem> getEntries(RequestContext request) throws ResponseContextException {
		return ProblemManager.getInstance().getOpenProblems();
	}

	@Override
	public Problem getEntry(String resourceName, RequestContext request) throws ResponseContextException {
		String correlator = resourceName.substring(ID_PREFIX.length());
		return ProblemManager.getInstance().getProblem(correlator);
	}

	@Override
	public String getId(Problem entry) throws ResponseContextException {
		return ID_PREFIX+entry.getCorrelator();
	}

	@Override
	public String getName(Problem entry) throws ResponseContextException {
//		return entry.getTitle().replaceAll(" ", "_");
		return entry.getCorrelator();
	}

	@Override
	public String getTitle(Problem entry) throws ResponseContextException {
		return entry.getTitle();
	}

	@Override
	public Date getUpdated(Problem entry) throws ResponseContextException {
		return entry.getLastUpdateDate();
	}

	@Override
	public Problem postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public void putEntry(Problem entry, String title, Date updated, List<Person> authors, String summary, Content content,
			RequestContext request) throws ResponseContextException {
		throw new ResponseContextException(404);
	}

	@Override
	public String getAuthor(RequestContext request) throws ResponseContextException {
		return "E2open SMI";
	}

	@Override
	public String getId(RequestContext request) {
		return "tag:smi.e2open.com,2009:problem:feed";
	}

	public String getTitle(RequestContext request) {
		return "Problem List";
	}

}
