package sk.feromakovi.ldatrainer.utils;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import uk.ac.open.crc.intt.IdentifierNameTokeniser;
import uk.ac.open.crc.intt.IdentifierNameTokeniserFactory;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public final class SourceCode {
	
	public static final CompilationUnit parse(final File file) throws Exception{
		return JavaParser.parse(new FileInputStream(file));
	}
	
	public static final String[] tokenize(final File sourceFile){
		String[] tokens = new String[0];
		try {
			tokens = tokenize(Files.toString(sourceFile, Charsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tokens;
	}
	
	public static final String[] tokenize(final String sourceCode){ //sourceCode.replaceAll("[(){}\n\",._':;?&!=@+/*<>]", " ").replaceAll("\\s+", " ").split("\\s");
		IdentifierNameTokeniserFactory factory = new IdentifierNameTokeniserFactory();
		factory.setSeparatorCharacters(SEPARATORS);
		IdentifierNameTokeniser tokeniser = factory.create();
		String[] tokens = tokeniser.tokenise(sourceCode);
		StringBuilder builder = new StringBuilder();
		for(String t : tokens)
			builder.append(t + " ");		
		String tokenized = builder.toString().replaceAll("\n", " ");
		Iterator<String> iterator = WORDS.iterator();
		while(iterator.hasNext())
			tokenized = tokenized.replaceAll("\\b" + iterator.next() + "\\b", "");
		tokenized = tokenized.replaceAll("\\s+", " ");
		if(tokenized.startsWith(" "))
			tokenized = tokenized.replaceFirst(" ", "");		
		return tokenized.split(" ");
		
	}
	
	public static void main(String... args) throws IOException{
		String code = Files.toString(Paths.get("/Users/feromakovi/Desktop/token.java").toFile(), Charset.defaultCharset());
		
		for(String l : tokenize(code))
			System.out.println(l);
	}
	
	
	private static final String SEPARATORS = "$_,-.:\"'(){}[]=<>;@+/*";
	
	private static final Set<String> WORDS = new HashSet<String>();
	static{
		WORDS.add("get");
		WORDS.add("set");
		WORDS.add("to");
		WORDS.add("new");
		WORDS.add("public");
		WORDS.add("private");
		WORDS.add("protected");
		WORDS.add("static");
		WORDS.add("final");
		WORDS.add("synchronized");
		WORDS.add("volatile");
		WORDS.add("transient");
		WORDS.add("final");
		WORDS.add("void");
		WORDS.add("abstract");
		WORDS.add("instanceof");
		WORDS.add("try");
		WORDS.add("catch");
		WORDS.add("finally");
		WORDS.add("import");
		WORDS.add("return");
		WORDS.add("super");
		WORDS.add("for");
		WORDS.add("do");
		WORDS.add("while");
		WORDS.add("repeat");
		WORDS.add("until");
		WORDS.add("if");
		WORDS.add("else");
		WORDS.add("class");
		WORDS.add("true");
		WORDS.add("false");
		WORDS.add("assert");
		WORDS.add("break");
		WORDS.add("case");
		WORDS.add("const");
		WORDS.add("continue");
		WORDS.add("default");
		WORDS.add("enum");
		WORDS.add("extends");
		WORDS.add("goto");
		WORDS.add("implements");
		WORDS.add("interface");
		WORDS.add("native");
		WORDS.add("null");
		WORDS.add("package");
		WORDS.add("strictfp");
		WORDS.add("switch");
		WORDS.add("this");
		WORDS.add("throw");
		WORDS.add("throws");
		WORDS.add("boolean");
		WORDS.add("byte");
		WORDS.add("char");
		WORDS.add("double");
		WORDS.add("float");
		WORDS.add("int");
		WORDS.add("long");
		WORDS.add("short");
	}
}