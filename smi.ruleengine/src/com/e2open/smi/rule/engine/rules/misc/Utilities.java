package com.e2open.smi.rule.engine.rules.misc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.e2open.smi.rule.engine.CEPServer;
import com.e2open.smi.rule.engine.ExpressionResolverMgr;
import com.e2open.smi.rule.engine.eif.event.Event;
import com.e2open.smi.rule.engine.rules.ExpressionResolver;



public class Utilities {
	private static final String UNKNOWN = "UNKNOWN";

	public static String md5(String input) {
		return digest("MD5", input);
	}

	public static String sha1(String input) {
		return digest("SHA", input);
	}

	public static String digest(String algorithm, String input) {
		String signature = UNKNOWN;
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.reset();
			byte[] buffer = input.getBytes();
			md.update(buffer);
			byte[] digest = md.digest();

			String hexStr = "";
			for (int i = 0; i < digest.length; i++) {
				hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
			}
			signature = hexStr;
		} catch (NoSuchAlgorithmException e) {
			CEPServer.getLogger().warning("Unable to get MD5 Digest: " + e.getMessage());
		}
		return signature;
	}
	
	private static final String parameterRegEx = "(&.*?;)";
	private static final Pattern compiledParameterRegEx = Pattern.compile(parameterRegEx);

	public static String parse(String param, Event e) {
		String resolved = param;
		
		HashMap<String, String> varAns = null;
		varAns = extractVariables(param);
		varAns = resolve(varAns, e);
		resolved = insertAnswers(param, varAns);
		return resolved;
	}

	private static String insertAnswers(String param, HashMap<String, String> varAns) {
		String output = new String(param), inputStr =param;
		Matcher matcher = compiledParameterRegEx.matcher(inputStr);
		boolean matchFound = matcher.find();
		if (matchFound) {
			do {
				matcher = compiledParameterRegEx.matcher(inputStr);
				matchFound = matcher.find();

				String variable = "";
				if (matchFound) {
					variable = matcher.group(1);
					variable = variable.trim();
					String answer = varAns.get(variable);
					Matcher matcher2 = Pattern.compile(variable).matcher(output);
					output = matcher2.replaceAll(answer);
					inputStr = inputStr.substring(matcher.end());
				}
			} while (matchFound && inputStr.length() > 0);
		}
		return output;
	}

	private static HashMap<String, String> extractVariables(String inputStr) {
		HashMap<String, String> varAns = new HashMap<String, String>();

		Matcher matcher = compiledParameterRegEx.matcher(inputStr);
		boolean matchFound = matcher.find();
		if (matchFound) {
			do {
				matcher = compiledParameterRegEx.matcher(inputStr);
				matchFound = matcher.find();

				String variable = "", value = "";
				if (matchFound) {
					variable = matcher.group(1);
					variable = variable.trim();
					varAns.put(variable, value);
					inputStr = inputStr.substring(matcher.end());
				}
			} while (matchFound && inputStr.length() > 0);
		}
		return varAns;
	}

	private static HashMap<String, String> resolve(HashMap<String, String> varAns, Event e) {
		HashMap<String, String> orgVarAns = new HashMap<String, String>(varAns);
		for (ExpressionResolver resolver : ExpressionResolverMgr.getInstance().getResolvers()) {
			Pattern compiledRegEx = Pattern.compile(resolver.getRegEx());
			String answer = "";
			for (String expression : orgVarAns.keySet()) {
				Matcher matcher = compiledRegEx.matcher(expression);
				if(matcher.find()){
					answer = resolver.resolve(expression, e);
					varAns.put(expression, answer);
				}
			}
		}
		return varAns;
	}

	
}
