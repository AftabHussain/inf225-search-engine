package edu.uci.ics.inf225.searchengine.search.scoring.solvers;

import java.util.List;
import java.util.Map;

import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;

public abstract class ScoringContributor<T> {

	private DocScorerUpdater<T> scoreUpdater;

	public ScoringContributor(DocScorerUpdater<T> scoreUpdater) {
		this.scoreUpdater = scoreUpdater;
	}

	public abstract void score(List<? extends Object> allQueryTerms, Map<Object, PostingsList> postingsLists, MultiFieldTermIndex termIndex, DocumentIndex docIndex, QueryScorer queryScorer,
			String field);

	public DocScorerUpdater<T> getScoreUpdater() {
		return scoreUpdater;
	}
}
