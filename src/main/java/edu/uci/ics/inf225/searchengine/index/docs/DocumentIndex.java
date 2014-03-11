package edu.uci.ics.inf225.searchengine.index.docs;

import java.util.Set;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.TermIndex;

public interface DocumentIndex {

	public int addDoc(WebPage page);

	public int count();

	public WebPage getDoc(int docID);

	public void prepare(TermIndex termIndex);

	public void shutdown();

	public void addTerms(int docID, Set<Integer> termIDs);

	public Set<Integer> getTerms(int docID);
}
