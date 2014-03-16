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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.open.crc.intt.IdentifierNameTokeniser;
import uk.ac.open.crc.intt.IdentifierNameTokeniserFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public final class SourceCode {
	
	private static final String SEPARATORS = "~^$&|?\\_,-.:\"'(){}[]=<>;%@+/*#!1234567890";
	static Pattern mPackagePattern = Pattern.compile("package (.*?);");
	
	public static final CompilationUnit parse(final File file){
		CompilationUnit cu = null;
		try{
			cu = JavaParser.parse(new FileInputStream(file));
			return cu;
		}catch(Exception e){}
		return null;
	}
	
	public static final String representationOf(final String delimiter, final String... strings){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < strings.length; i++){
			builder.append(strings[i]);
			if(i < (strings.length -1))
				builder.append(delimiter);
		}
		return builder.toString();
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
	
	public static final String extractPackage(final File sourceFile){
		String extractedPackage = null;
		try {
			extractedPackage = extractPackage(Files.toString(sourceFile, Charsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return extractedPackage;
	}
	
	public static final String extractPackage(final String text){
		Matcher matcher = mPackagePattern.matcher(text);
		if(matcher.find())
			return matcher.group(1);
		return null;
	}
	
	public static final String[] tokenize(final String sourceCode){ //sourceCode.replaceAll("[(){}\n\",._':;?&!=@+/*<>]", " ").replaceAll("\\s+", " ").split("\\s");
		IdentifierNameTokeniserFactory factory = new IdentifierNameTokeniserFactory();
		factory.setSeparatorCharacters(SEPARATORS);
		IdentifierNameTokeniser tokeniser = factory.create();
		String[] tokens = tokeniser.tokenise(sourceCode);
		StringBuilder builder = new StringBuilder();
		for(String t : tokens)
			builder.append(t + " ");		
		String tokenized = builder.toString().replaceAll("\n", " ").toLowerCase();
		Iterator<String> iterator = StopWords.ALL.iterator();
		while(iterator.hasNext())
			tokenized = tokenized.replaceAll("\\b" + iterator.next().toLowerCase() + "\\b", "");
		tokenized = tokenized.replaceAll("\\b[^\\s]\\b", "");
		tokenized = tokenized.replaceAll("\\b.{2}\\b", "");
		tokenized = tokenized.replaceAll("\\s+", " ");
		if(tokenized.startsWith(" "))
			tokenized = tokenized.replaceFirst(" ", "");		
		return tokenized.split(" ");
	}
	
	public static void main(String... args) throws IOException{
		String code = Files.toString(Paths.get("/Users/feromakovi/Desktop/token.j").toFile(), Charset.defaultCharset());
//		System.out.println(extractPackage(code));
//		for(String l : tokenize(code))
//			System.out.println(l);
	}
}