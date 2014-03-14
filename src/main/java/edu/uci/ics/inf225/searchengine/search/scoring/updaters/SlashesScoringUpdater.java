package edu.uci.ics.inf225.searchengine.search.scoring.updaters;

import edu.uci.ics.inf225.searchengine.search.scoring.DocScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.DocScorerUpdater;

public class SlashesScoringUpdater implements DocScorerUpdater<Byte> {

	@Override
	public void update(DocScorer scorer, Byte value) {
		scorer.setNumberOfSlashes(value);
	}

}
