package com.e2open.smi.rule.engine.eif.event;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class Event {
	private HashMap<String, String> attributes = new HashMap<String, String>();

//	private enum HeaderAttribute {
//		UUID, TYPE, severity, hostname, date_reception, UPDATE_TIME, repeat_count, status, administrator, source, msg
//	};

	
	public enum HeaderAttribute {
		uuid, type, severity, hostname, date_reception, update_time, repeat_count, status, administrator, source, msg
	};

	public Event() {
		setUuid(UUID.randomUUID().toString());
		setType("EVENT");
		setSeverity(Severity.UNKNOWN);
		setHost("");
		setRepeatCount(0);
		setStatus(Status.OPEN);
		setOwner("");
		setSource("UNKNOWN");
		setMessage("");
		setCreateTime(new Date().getTime());
		setUpdateTime(getCreateTime());
	}

	public Event(Event e) {
		attributes.putAll(e.getAttributes());
	}

	public static final Slot UNKNOWN_SLOT = new Slot("UNKNOWN", "UNKNOWN");

//	public Slot getSlot(String name) {
//		return new Slot(name, getAttribute(name));
//	}

	public String getSlot(String name){
		return attributes.get(name);
	}
	
	public void setSlot(Slot slot) {
		addAttribute(slot.getName(), slot.getValue());
	};

	@Override
	public String toString() {
		return attributes.toString();
	}

	private void addHeader(HeaderAttribute key, String value) {
		attributes.put(key.name(), value);
		if (!HeaderAttribute.update_time.equals(key))
			setUpdateTime(new Date().getTime());
	}

	private String getHeader(HeaderAttribute key) {
		return attributes.get(key.name());
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, value);
		if (!HeaderAttribute.update_time.name().equals(key))
			setUpdateTime(new Date().getTime());
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
			addAttribute(key, "");
		return attributes.get(key);
	}

	private final static String SLOT_SEPERATOR = ";";

	public String toRawFMT() {
		String wireFMT = getType() + SLOT_SEPERATOR;
		for (String key : attributes.keySet()) {
			String value = attributes.get(key);
			if (value != null && value.length() > 0)
				wireFMT += key + "=" + value + SLOT_SEPERATOR;
		}
		wireFMT += "END\001";
		return wireFMT;
	}

	public String getUuid() {
		return getHeader(HeaderAttribute.uuid);
	}

	public void setUuid(String uuid) {
		addHeader(HeaderAttribute.uuid, uuid);
	}

	public String getType() {
		return getHeader(HeaderAttribute.type);
	}

	public void setType(String type) {
		addHeader(HeaderAttribute.type, type);
	}

	public Severity getSeverity() {
		return Severity.valueOf(getHeader(HeaderAttribute.severity));
	}

	public void setSeverity(Severity severity) {
		addHeader(HeaderAttribute.severity, severity.name());
	}

	public String getHost() {
		return getHeader(HeaderAttribute.hostname);
	}

	public void setHost(String host) {
		addHeader(HeaderAttribute.hostname, host);
	}

	public long getCreateTime() {
		return Long.valueOf(getHeader(HeaderAttribute.date_reception)).longValue();
	}

	public String getCreateTimeFormatted(String fmt) {
		String fmtString = "E, dd MMM yyyy HH:mm:ss Z";
		if (fmt != null && fmt.length() > 0)
			fmtString = fmt;
		Format formatter = new SimpleDateFormat(fmtString);
		String s = formatter.format(getCreateTime());
		return s;
	}

	public void setCreateTime(long createTime) {
		addHeader(HeaderAttribute.date_reception, Long.toString(createTime));
	}

	public long getUpdateTime() {
		return Long.valueOf(getHeader(HeaderAttribute.update_time)).longValue();
	}

	public String getUpdateTimeFormatted(String fmt) {
		String fmtString = "E, dd MMM yyyy HH:mm:ss Z";
		if (fmt != null && fmt.length() > 0)
			fmtString = fmt;
		Format formatter = new SimpleDateFormat(fmtString);
		String s = formatter.format(getUpdateTime());
		return s;
	}

	public void setUpdateTime(long updateTime) {
		addHeader(HeaderAttribute.update_time, Long.toString(updateTime));
	}

	public long getRepeatCount() {
		return Long.valueOf(getHeader(HeaderAttribute.repeat_count)).longValue();
	}

	public void setRepeatCount(long repeatCount) {
		addHeader(HeaderAttribute.repeat_count, Long.toString(repeatCount));
	}

	public Status getStatus() {
		return Status.valueOf(getHeader(HeaderAttribute.status));
	}

	public void setStatus(Status status) {
		addHeader(HeaderAttribute.status, status.name());
	}

	public String getOwner() {
		return getHeader(HeaderAttribute.administrator);
	}

	public void setOwner(String owner) {
		addHeader(HeaderAttribute.administrator, owner);
	}

	public String getSource() {
		return getHeader(HeaderAttribute.source);
	}

	public void setSource(String source) {
		addHeader(HeaderAttribute.source, source);
	}

	public String getMessage() {
		return getHeader(HeaderAttribute.msg);
	}

	public void setMessage(String message) {
		addHeader(HeaderAttribute.msg, message);
	}

	public static boolean isHeaderAttributeKey(String key) {
		return wkas.contains(key.toUpperCase());
	}

	public void addHeaderAttribute(String key, String value) {
		addAttribute(key.toUpperCase(), value);
	}

	public HashMap<String, String> getAttributes() {
		HashMap<String, String> rc = new HashMap<String, String>(attributes);
		rc.remove(HeaderAttribute.type.name());
		return rc;
	}

	private static ArrayList<String> wkas = new ArrayList<String>();
	static {
		for (HeaderAttribute attr : HeaderAttribute.values()) {
			wkas.add(attr.name());
		}
	}
	public boolean containsAttribute(String slotName) {
		return attributes.containsKey(slotName);
	}

	public void addSlot(String slotName, String slotValue) {
		setSlot(new Slot(slotName, slotValue));		
	}

}
