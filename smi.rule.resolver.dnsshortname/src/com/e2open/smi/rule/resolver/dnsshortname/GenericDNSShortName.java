package com.e2open.smi.rule.resolver.dnsshortname;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;

public class GenericDNSShortName implements ExpressionResolver {

	public String getDescription() {
		return "&genericDNSShortName'xxx'; where xxx is the slot you want to shorten to the first period .";
	}

	public String getName() {
		return "genericDNSShortName";
	}

	private static final String parameterRegEx = "&genericDNSShortName'(.*?)';";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			String slotName = matcher.group(1).trim();
			String orgValue = e.getAttribute(slotName);
			if (orgValue.contains("."))
				value = orgValue.substring(0, orgValue.indexOf('.'));
		}
		return value;
	}

}
