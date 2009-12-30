package com.e2open.smi.rule.resolver.dnsshortname;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.e2open.smi.rule.engine.eif.event.Event;

public class E2openDNSShortNameTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testResolve_NoFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals(hostname));
	}

	@Test
	public void testResolve_nonE2openFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.b.c";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals(hostname));
	}

	@Test
	public void testResolve_E2openDevFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.dev.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("a"));
	}

	@Test
	public void testResolve_E2openStgFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.sjcus.prod.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("a"));
	}
	@Test
	public void testResolve_E2openBldFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.denus.prod.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("a"));
	}

	@Test
	public void testResolve_E2openSanJoseFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.sjca.prod.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("a"));
	}

	@Test
	public void testResolve_E2openChgFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "a.chg.prod.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("a"));
	}

	@Test
	public void testResolve_E2openAppFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "abc-app.dev.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("abc-app"));
	}

	@Test
	public void testResolve_E2openHackFQDN() {
		Event e = new Event();
		String hostnameSlot = Event.HeaderAttribute.hostname.name();
		String hostname = "abc.ibm.dev.e2open.com";
		e.addAttribute(hostnameSlot, hostname);
		E2openDNSShortName r = new E2openDNSShortName();
		String expression = "&e2openDNSShortName'" + hostnameSlot + "';";
		String shortName = r.resolve(expression, e);
		assertTrue(shortName.equals("abc.ibm"));
	}
}
