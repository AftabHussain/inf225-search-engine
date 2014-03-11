package edu.uci.ics.inf225.searchengine.search.scoring.solvers;

import java.util.List;
import java.util.Map;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;

public interface ScoringContributor {

	public void score(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, TermIndex termIndex, DocumentIndex docIndex, QueryScorer queryScorer);
}
