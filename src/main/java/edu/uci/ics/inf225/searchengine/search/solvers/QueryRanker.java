package edu.uci.ics.inf225.searchengine.search.solvers;

import java.util.List;
import java.util.Map;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public interface QueryRanker {

	public List<Integer> query(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, int limit, TermIndex termIndex, DocumentIndex docIndex);
}
