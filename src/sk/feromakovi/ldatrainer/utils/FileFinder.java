package sk.feromakovi.ldatrainer.utils;

import java.io.File;
import java.io.FilenameFilter;

public final class FileFinder {
	
	public static final String PATTERN_JAVA = ".+\\.java$";
	
	private FileFinderListener mListener = null;
	private RegexFilter mFilter = null;
	
	public FileFinder(String pattern){
		this.mFilter = new RegexFilter(pattern);
	}
	
	public FileFinder(){
		this(null);
	}
	
	public void find(File file){
		try{
			if(!callbackListener(file) && file.isDirectory()){
				File[] files = file.listFiles(mFilter);
				for(File f : files)
					find(f);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void find(String path){
		this.find(new File(path));
	}
	
	private boolean callbackListener(File file){
		if(file != null && file.isFile()){
			if(this.mListener != null)
				this.mListener.onFileFind(file);				
			return true;
		}
		return false;
	}
	
	public void setMatchPattern(String pattern){
		this.mFilter = new RegexFilter(pattern);
	}
	
	public void setRecursive(final boolean recursive){
		this.mFilter.setRecursive(recursive);
	}
	
	public void setFileFinderListener(FileFinderListener listener){
		this.mListener = listener;
	}
	
	public static interface FileFinderListener{
		public abstract void onFileFind(File file);
	}
	
	private final class RegexFilter implements FilenameFilter{
		
		private String mPattern = null;
		
		private boolean mRecursive = true;
		
		public RegexFilter(String pattern){
			this.mPattern = pattern;
		}
		
		public void setRecursive(final boolean recursive){
			this.mRecursive = recursive;
		}

		@Override
		public boolean accept(File dir, String name) {
			File f = new File(dir, name);
			if(f.isFile() && this.mPattern != null && !name.matches(mPattern))
				return false;
			else if(f.isDirectory() && !mRecursive)
				return false;
			return true;
		}
	}
	
//	public static void main(String... args){
//    	FileFinder ff = new FileFinder(FileFinder.PATTERN_JAVA);
//    	ff.setFileFinderListener(sourceFile);
//    	ff.find(new File(projectRes.getLocationURI().getPath()));
//	}
}
