package com.e2open.smi.rule.resolver.formattedcurrentdatetimewithparameter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;

public class FormattedCurrentDateTimeWithParameter implements ExpressionResolver {

	public String getDescription() {
		return "&formattedCurrentDateTime'xxx'; where xxx is your favorite format.";
	}

	public String getName() {
		return "FormattedCurrentDateTimeWithParameterResolver";
	}

	private static final String parameterRegEx = "&formattedCurrentDateTime'(.*?)';";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public String getRegEx() {
		return parameterRegEx;
	}

	public String resolve(String expression, Event e) {
		Matcher matcher = compiledParameterRegEx.matcher(expression);
		String value = expression;
		if (matcher.find()) {
			String fmtString = matcher.group(1).trim();
			Format formatter = new SimpleDateFormat(fmtString);
			value = formatter.format(new Date().getTime());
		}
		return value;
	}
}
