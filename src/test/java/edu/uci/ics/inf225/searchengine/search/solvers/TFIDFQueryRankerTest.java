package edu.uci.ics.inf225.searchengine.search.solvers;

import java.util.Arrays;
import java.util.List;

public class TFIDFQueryRankerTest extends QueryRankerTest {

	@Override
	protected QueryRanker createQueryRanker() {
		return new TFIDFQueryRanker();
	}

	@Override
	protected List<Integer> expectedRankingDocs() {
		return Arrays.asList(new Integer[] { 5, 2, 1, 4, 3 });
	}
}