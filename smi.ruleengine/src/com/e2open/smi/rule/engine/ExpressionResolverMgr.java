package com.e2open.smi.rule.engine;

import java.util.ArrayList;
import java.util.List;

import com.e2open.smi.rule.engine.rules.ExpressionResolver;

public class ExpressionResolverMgr {
	private static ExpressionResolverMgr erm = new ExpressionResolverMgr();
	private ArrayList<ExpressionResolver> resolvers = new ArrayList<ExpressionResolver>();

	public static ExpressionResolverMgr getInstance() {
		return erm;
	}

	public void addExpressionResolver(ExpressionResolver expressionResolver) {
//		addExpressionResolver(expressionResolver);
		System.out.println("Adding Expression Resolver: "+expressionResolver.getName());
		resolvers.add(expressionResolver);
	}

	public void removeExpressionResolver(ExpressionResolver expressionResolver) {
		resolvers.remove(expressionResolver);
	}

	public List<ExpressionResolver> getResolvers(){
		ArrayList<ExpressionResolver> erl = new ArrayList<ExpressionResolver>(resolvers);
		return erl;
	}
}
