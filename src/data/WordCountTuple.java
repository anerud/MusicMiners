package data;

public class WordCountTuple implements Comparable<WordCountTuple> {
	
	private String word;
	private int count;
	private int id;
	
	public WordCountTuple(String word, int id, int count){
		this.word = word;
		this.id  = id;
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCount() {
		return count;
	}
	
	public int getID() {
		return id;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(WordCountTuple o) {
		return count - o.count;
	}

}
