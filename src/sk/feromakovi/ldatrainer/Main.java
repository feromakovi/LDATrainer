package sk.feromakovi.ldatrainer;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import sk.feromakovi.ldatrainer.utils.FileFinder;
import sk.feromakovi.ldatrainer.utils.FileFinder.FileFinderListener;
import sk.feromakovi.ldatrainer.utils.SourceCode;

public class Main{
	
    @Argument
    private List<String> arguments = new ArrayList<String>();
	
	@Option(name="-p")     
    private String p = ".";
	
	@Option(name="-o")     
    private String o = "output.txt";
	
	@Option(name="-v")     
    private boolean v = false;
	
	private long mEntriesCount = 0;
	
	FileFinderListener fileFinderListener = new FileFinderListener() {
		
		@Override
		public void onFileFind(File file) {
			try {
				log("onFileFind: " + file.getAbsolutePath());
				CompilationUnit compilationUnit = SourceCode.parse(file);
				if(compilationUnit != null)
					new ClassVisitor().visit(compilationUnit, null);
			} catch (Exception e) {}
		}
	};
	
	public Main(String[] args){
		CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        try {
            parser.parseArgument(args);            
            if( arguments.isEmpty() )
                throw new IllegalArgumentException("No argument is given");
        } catch( CmdLineException | IllegalArgumentException e ) {
            return;
        }        
	}
	
	public void proceed(){
		File out = new File(o); 
		if(out.exists())
			out.delete();
		try{
			out.createNewFile();
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		FileFinder fileFinder = new FileFinder(FileFinder.PATTERN_JAVA);
    	fileFinder.setRecursive(true);
    	fileFinder.setFileFinderListener(fileFinderListener);
    	fileFinder.find(p);
    	
    	RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File(o), "rw");
			f.seek(0); 
	    	f.write(new String(Long.toString(mEntriesCount) + "\n").getBytes());
	    	f.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
	}
	
	private class ClassVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        	try{
        		String body = n.toString();
        		String[] tokens = SourceCode.tokenize(body);
        		if(tokens != null && tokens.length > 10){
        			String modelLine = SourceCode.representationOf(" ", tokens);
        			appendToOutput(modelLine.toLowerCase());
        		}
        	}catch(Exception e){}        
		}       
    }
	
	private void appendToOutput(final String line){
		if(line != null && line.length() > 0){
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(o, true)));
			    out.println(line);
			    out.close();
			    mEntriesCount++;
			} catch (IOException e) {
			   e.printStackTrace();
			}
		}
	}
	
	public void log(String log){
		if(this.v)
			System.out.println(log);
	}
	
	public static void main(String[] args) {
    	Main main = new Main(args);
    	main.proceed();    	
	}
}
