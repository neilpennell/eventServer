package com.e2open.smi.rule.resolver.slot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;

public class Slot implements ExpressionResolver {
	private static final String parameterRegEx = "&slot\\.(.*?);";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			String slotName = matcher.group(1).trim();
			if (e.containsAttribute(slotName))
				value = e.getAttribute(slotName);
		}
		return value;
	}

	public String getDescription() {
		return "Return the value of a slot from and event.  Slots are specified like &slot.x; where x is the slot name you want.  To get the Event message use &slot.msg;";
	}

	public String getName() {
		return "SlotResolver";
	}
}
