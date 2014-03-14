package edu.uci.ics.inf225.searchengine.search.scoring.solvers;

import edu.uci.ics.inf225.searchengine.search.scoring.DocScorer;

public interface DocScorerUpdater<T> {

	public void update(DocScorer scorer, T value);

}
