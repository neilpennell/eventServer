package com.e2open.smi.rule.resolver.formattedcurrentdatetime;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;

public class FormattedCurrentDateTime implements ExpressionResolver {

	public String getDescription() {
		return "returns the current time in the following format 'E, dd MMM yyyy HH:mm:ss Z'.  &formattedCurrentDateTime;";
	}

	public String getName() {
		return "FormattedCurrentDateTimeResolver";
	}

	private static final String parameterRegEx = "&formattedCurrentDateTime;";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);
	private static final String fmtString = "E, dd MMM yyyy HH:mm:ss Z";

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			Format formatter = new SimpleDateFormat(fmtString);
			value = formatter.format(new Date().getTime());
		}
		return value;
	}

}
