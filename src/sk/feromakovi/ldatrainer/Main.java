package sk.feromakovi.ldatrainer;

import static org.kohsuke.args4j.ExampleMode.ALL;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import sk.feromakovi.ldatrainer.utils.FileFinder;
import sk.feromakovi.ldatrainer.utils.FileFinder.FileFinderListener;
import sk.feromakovi.ldatrainer.utils.SourceCode;

public class Main implements FileFinderListener{
	
	private static final int MIN_TOKENS_COUNT = 10;
	
    @Argument
    private List<String> mArguments = new ArrayList<String>();
	
	@Option(name="-p", usage="path where should start finding")     
    private String mPath = ".";
	
	@Option(name="-o", usage = "output file")     
    private String mOutputFile = "output.txt";
	
	@Option(name="-v", usage = "verbose mode, allows print more information to console")     
    private boolean mVerbose = false;
	
	@Option(name="-s", usage = "print statistic in the end")     
    private boolean mStatistic = false;
	
	@Option(name="-h", usage = "help")     
    private boolean mHelp = false;
	
	private long mFoundClassCount = 0;
	
	private long mEntriesCount = 0;
	
	private Set<String> mPackages = new HashSet<String>();
	
	public Main(String[] args){
		CmdLineParser parser = new CmdLineParser(this);
        parser.setUsageWidth(80);
        try {
            parser.parseArgument(args);            
            if( mArguments.isEmpty() )
                throw new IllegalArgumentException("No argument is given");
            handleHelp(parser);
        } catch( CmdLineException | IllegalArgumentException e ) {
            return;
        }        
	}
	
	public void proceed(){
		File out = new File(mOutputFile); 
		if(out.exists())
			out.delete();
		try{
			out.createNewFile();
		}catch(Exception e){e.printStackTrace();}		
		
		FileFinder fileFinder = new FileFinder(FileFinder.PATTERN_JAVA);
    	fileFinder.setRecursive(true);
    	fileFinder.setFileFinderListener(this);
    	fileFinder.find(mPath);
    	
    	RandomAccessFile f;
		try {
			f = new RandomAccessFile(new File(mOutputFile), "rw");
			f.seek(0); 
	    	f.write(new String(Long.toString(mEntriesCount) + "\n").getBytes());
	    	f.close();
		} catch (Exception e) {e.printStackTrace();}  
		if(mStatistic){
			this.mVerbose = Boolean.TRUE;
			log("All packages: " + this.mPackages.size());
			log("All classes: " + this.mFoundClassCount);
			log("Used classes: " + mEntriesCount);
		}			
	}
	
	@Override
	public void onFileFind(File file) {
		try {
			log(file.getAbsolutePath());
			if(mStatistic){
				String pckg = SourceCode.extractPackage(file);
				mPackages.add(pckg);
				mFoundClassCount++;
			}
			CompilationUnit compilationUnit = SourceCode.parse(file);
			if(compilationUnit != null)
				new ClassVisitor().visit(compilationUnit, null);			
		} catch (Exception e) {}
	}
	
	private void appendToOutput(final String line){
		if(line != null && line.length() > 0){
			try {
			    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mOutputFile, true)));
			    out.println(line);
			    out.close();
			    mEntriesCount++;
			} catch (IOException e) {
			   e.printStackTrace();
			}
		}
	}
	
	public void log(String log){
		if(this.mVerbose)
			System.out.println(log);
	}
	
	private boolean handleHelp(CmdLineParser parser){
		if(this.mHelp){
			parser.printUsage(System.err);
	        System.err.println();
	        System.err.println("  Example: java SampleMain"+parser.printExample(ALL));
		}
		return this.mHelp;
	}
	
	private class ClassVisitor extends VoidVisitorAdapter {
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        	try{
        		String body = n.toString();
        		String[] tokens = SourceCode.tokenize(body);
        		if(tokens != null && tokens.length > MIN_TOKENS_COUNT){
        			String modelLine = SourceCode.representationOf(" ", tokens);
        			appendToOutput(modelLine.toLowerCase());
        		}
        	}catch(Exception e){}        
		}       
    }
	
	public static void main(String[] args) {
    	Main main = new Main(args);
    	main.proceed();    	
	}	
}
