package edu.uci.ics.inf225.searchengine.similarity;
import java.util.List;
import java.util.Set;

/**
 * @author zhangcheng
 *
 */
public interface IWordSeg {

	public List<String> tokens(String doc);
	
	public List<String> tokens(String doc, Set<String> stopWords);
}