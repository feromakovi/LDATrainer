package sk.feromakovi.ldatrainer.utils;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class ClassVisitor extends VoidVisitorAdapter {
	
	private static final int MIN_TOKENS_COUNT = 10;
	
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
				this.mCode = SourceCode.representationOf(" ", tokens);
			}
		}catch(Exception e){}
	}
}
