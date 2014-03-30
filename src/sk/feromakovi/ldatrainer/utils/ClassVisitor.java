package sk.feromakovi.ldatrainer.utils;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.feromakovi.ldatrainer.models.Term;

public class ClassVisitor extends VoidVisitorAdapter {
	
	private static final int MIN_TOKENS_COUNT = 10;
	
	private Map<String, Term> mTermFrequency = new HashMap<String, Term>();
	
	private String mCode;
	
	public void reset(){
		this.mCode = null;
	}
	
	public void reset(CompilationUnit compilationUnit){
		reset();
		visit(compilationUnit, null);
	}
	
	public String getParsedCode(){
		return this.mCode;
	}
	
	public void visit(ClassOrInterfaceDeclaration n, Object arg){
		try{
			final String originalCode = n.toString();
			final String noJavaCode = SourceCode.removeSet(SourceCode.removeSeparators(originalCode), StopWords.JAVA, false);
			final String[] tokens = SourceCode.tokenize(noJavaCode);
			if (tokens != null && tokens.length > MIN_TOKENS_COUNT) {
				updateTermFrequency(tokens);
				this.mCode = SourceCode.representationOf(" ", tokens);
			}
		}catch(Exception e){}
	}

	private void updateTermFrequency(String[] tokens) {
		for(String token : tokens){
			if(this.mTermFrequency.containsKey(token))
				this.mTermFrequency.get(token).inc();
			else
				this.mTermFrequency.put(token, new Term(token));
		}
	}
	
	public List<Term> getTermFrequency(){
		List<Term> terms = new ArrayList<>(this.mTermFrequency.values());
		Collections.sort(terms);
		return terms;
	}
}
