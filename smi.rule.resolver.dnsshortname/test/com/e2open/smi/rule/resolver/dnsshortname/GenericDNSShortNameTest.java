package com.e2open.smi.rule.resolver.dnsshortname;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.e2open.smi.rule.engine.eif.event.Event;

public class GenericDNSShortNameTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testResolveNoFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		e.addAttribute(hostnameSlot, "a");
		GenericDNSShortName r = new GenericDNSShortName();
		String expression = "&genericDNSShortName'"+hostnameSlot+"';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals(expression));
	}

	@Test
	public void testResolveFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		e.addAttribute(hostnameSlot, "a.b.c");
		GenericDNSShortName r = new GenericDNSShortName();
		String shortName = r.resolve("&genericDNSShortName'"+hostnameSlot+"';", e);
		assertTrue(shortName.equals("a"));
	}

}
