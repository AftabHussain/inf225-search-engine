package edu.uci.ics.inf225.searchengine.similarity;

public interface SimilarityFilter {
	
	public boolean isDuplicate(String url, String pageContent);

}
