package com.e2open.smi.rule.problems;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.e2open.smi.rule.engine.eif.event.Status;

public class Problem {
	private Date createDate, lastUpdateDate, lastLogUpdateDate;
	private ArrayList<Entry> entries = new ArrayList<Entry>();
	private HashMap<String, String> attributes = new HashMap<String, String>();
//	private String fmtString = "E, dd MMM yyyy HH:mm:ss Z"; // ex. Thu, 23 Apr 2009 06:36:36 -0500
//	private String fmtString = "dd MMM yyyy HH:mm:ss.SSS z"; // ex: 23 Apr 2009 22:36:26.465 CDT
	private String fmtString = "yyyy-MM-dd HH:mm:ss.SSS z";

	private class Entry {
		Date timeStamp;
		String entry;

		public String getTimeFormatted(String fmt) {
			if (fmt != null && fmt.length() > 0)
				fmtString = fmt;
			Format formatter = new SimpleDateFormat(fmtString);
			String s = formatter.format(timeStamp);
			return s;
		}
	}

	public enum key {
		CORRELATOR, TITLE, OWNER, STATUS
	};

	private final String CID = "CID";

	public Problem(String correlator) {
		createDate = new Date();
		addAttribute(CID, correlator);
		setStatus(Status.OPEN);
	}

	public void addAttribute(String key, String value) {
		String oldValue = getAttribute(key);
		attributes.put(key, value);
		addEntry(key + " Changed: old=" + oldValue + " new=" + value);
		lastUpdateDate = new Date();
	}

	/**
	 * if the key does not exist in the event then "" (empty string) is
	 * returned.
	 * 
	 * @param key
	 * @return
	 */
	public String getAttribute(String key) {
		if (!attributes.containsKey(key))
			attributes.put(key, "");
		// addAttribute(key, "");
		return attributes.get(key);
	}

	public HashMap<String, String> getAttributes() {
		HashMap<String, String> rc = new HashMap<String, String>(attributes);
		return rc;
	}

	public boolean containsAttribute(String slotName) {
		return attributes.containsKey(slotName);
	}

	public String getCorrelator() {
		return getAttribute(CID);
	}

	public Date getCreateDate() {
		return createDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public Date getLastLogUpdateDate() {
		return lastLogUpdateDate;
	}

	public List<Entry> getLogEntries() {
		ArrayList<Entry> rc = new ArrayList<Entry>(entries);
		return rc;
	}

	public void addEntry(Entry entry) {
		lastLogUpdateDate = new Date();
		lastUpdateDate = lastLogUpdateDate;
		entries.add(entry);
	}

	public void addEntry(String logEntry) {
		Entry entry = new Entry();
		entry.timeStamp = new Date();
		entry.entry = logEntry;
		this.addEntry(entry);
	}

	public String getTitle() {
		return getAttribute(key.TITLE.name());
	}

	public void setTitle(String title) {
		// String oldTitle = getTitle();
		addAttribute(key.TITLE.name(), title);
		// addEntry("Title Change: old="+oldTitle+" new="+title);
	}

	@Override
	public String toString() {
		return getCorrelator() + " : " + getStatus() + " : " + getTitle();
	}

	public void setOwner(String owner) {
		// String oldOwner = getOwner();
		addAttribute(key.OWNER.name(), owner);
		// addEntry("Owner Change: old="+oldOwner+" new="+owner);
	}

	public String getOwner() {
		return getAttribute(key.OWNER.name());
	}

	public Status getStatus() {
		return Status.valueOf(getAttribute(key.STATUS.name()));
	}

	public void setStatus(Status status) {
		// Status oldStatus = getStatus();
		addAttribute(key.STATUS.name(), status.name());
		// addEntry("Status Change: old="+oldStatus.name()+" new="+status.name());
	}

	public String toHTML() {
		String html = "<div xmlns=\"http://www.w3.org/1999/xhtml\">" + "<div>" + "<table>";
		// + "<tr>" + "<td>Key</td>"
		// + "<td>Value</td>" + "</tr>";
		for (String key : getAttributes().keySet()) {
			html += "<tr>" + "<td>" + key + "</td>" + "<td>" + getAttribute(key) + "</td>" + "</tr>";
		}
		for (Entry entry : getLogEntries()) {
			html += "<tr>" + "<td>" + entry.getTimeFormatted(null) + "</td>" + "<td>" + entry.entry + "</td>" + "</tr>";
		}
		html += "</table>" + "</div>" + "</div>";
		return html;
	}
}
