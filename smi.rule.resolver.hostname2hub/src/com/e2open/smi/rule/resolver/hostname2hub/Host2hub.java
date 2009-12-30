package com.e2open.smi.rule.resolver.hostname2hub;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;
import com.e2open.smi.rule.engine.rules.misc.Utilities;

public class Host2hub implements ExpressionResolver {
	private HashMap<String, String> host2hubMap = new HashMap<String, String>();

	public Host2hub() {
		host2hubMap.put("host1", "hubA");
		host2hubMap.put("host2", "hubB");
	}

	public String getDescription() {
		return "&hostname2hub'xxx'; where xxx is the slot you want to use to map into hub.";
	}

	public String getName() {
		return "hostname2hub";
	}

	private static final String parameterRegEx = "&hostname2hub'(.*?)';";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			String slotName = Utilities.parse(matcher.group(1).trim(), e);
			// String slotName = matcher.group(1).trim();
			String orgValue = Utilities.parse(e.getAttribute(slotName), e);
			value = host2hubMap.get(orgValue);
			if (null == value)
				value = "UNKNOW";
		}
		return value;
	}

}
