package edu.uci.ics.inf225.searchengine.similarity;
import java.util.List;
import java.util.Set;

/**
 * Code taken from https://github.com/sing1ee/simhash-java.
 *
 */
public interface IWordSeg {

	public List<String> tokens(String doc);
	
	public List<String> tokens(String doc, Set<String> stopWords);
}
