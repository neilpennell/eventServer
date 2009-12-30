package com.e2open.smi.rule.resolver.dnsshortname;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;
import com.e2open.smi.rule.engine.rules.misc.Utilities;

public class E2openDNSShortName implements ExpressionResolver {
	public static final String[] e2openDomainNames = { "dev.e2open.com", "sjcus.prod.e2open.com", "denus.prod.e2open.com",
			"sjca.prod.e2open.com", "chg.prod.e2open.com" };

	public String getDescription() {
		return "&e2openDNSShortName'xxx'; where xxx is the slot you want to shorten to a valid e2open short name, will return FQDN if not an e2open domain name.";
	}

	public String getName() {
		return "e2openDNSShortName";
	}

	private static final String parameterRegEx = "&e2openDNSShortName'(.*?)';";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			 String slotName = Utilities.parse(matcher.group(1).trim(),e);
//			String slotName = matcher.group(1).trim();
			String orgValue = Utilities.parse(e.getAttribute(slotName), e);
			value = orgValue;
			for (String dn : e2openDomainNames) {
				if (orgValue.contains(dn)) {
					value = orgValue.substring(0, orgValue.indexOf(dn) - 1);
					break;
				}
			}
		}
		return value;
	}

}
