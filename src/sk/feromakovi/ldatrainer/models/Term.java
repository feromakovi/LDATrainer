package sk.feromakovi.ldatrainer.models;

public final class Term implements Comparable<Term>{
	
	private String mTerm;
	
	private int mFrequency = 1;
	
	public Term(String term){
		this.mTerm = term;
	}
	
	public int getFrequency(){
		return this.mFrequency;
	}
	
	public void inc(){
		this.mFrequency++;
	}
	
	@Override
	public boolean equals(Object obj) {
		return toString().equalsIgnoreCase(obj.toString());
	}
	
	@Override
	public String toString() {
		return this.mTerm;
	}

	@Override
	public int compareTo(Term o) {
		if(this.mFrequency < o.getFrequency()) return -1;
		else if(this.mFrequency > o.getFrequency()) return 1;
		else return 0;
	}
}
