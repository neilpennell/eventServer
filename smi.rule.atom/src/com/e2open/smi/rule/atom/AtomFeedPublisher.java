package com.e2open.smi.rule.atom;

import java.util.Dictionary;

import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.osgi.service.http.HttpContext;

public abstract class AtomFeedPublisher {
	private ClassLoader ldr = this.getClass().getClassLoader();
	public ClassLoader getClassLoader() { return ldr; }
	public abstract String getName();
	public String getDescription() {return "";}
	public abstract AbderaServlet getServlet();
	public Dictionary<String, Object> getInitParams() { return null; }
	public HttpContext getContext() { return null; }
	public abstract String getAlias();
}
