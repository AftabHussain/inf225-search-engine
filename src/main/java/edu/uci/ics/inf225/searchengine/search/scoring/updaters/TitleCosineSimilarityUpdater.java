package edu.uci.ics.inf225.searchengine.search.scoring.updaters;

import edu.uci.ics.inf225.searchengine.search.scoring.DocScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.DocScorerUpdater;

public class TitleCosineSimilarityUpdater implements DocScorerUpdater<Double> {

	@Override
	public void update(DocScorer scorer, Double value) {
		scorer.setTitleCosineSimilarity(value);
	}

}
