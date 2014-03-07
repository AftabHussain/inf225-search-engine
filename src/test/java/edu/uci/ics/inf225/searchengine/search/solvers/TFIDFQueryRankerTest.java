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
		// 1: 13.1 + 3.0 = 16.1
		// 2: 11.4 + 8.3 = 19.7
		// 3: 0
		// 4: 1.0 = 1.0
		// 5:
		// 6:
		return Arrays.asList(new Integer[] { 2, 1, 4 });
	}

	@Override
	protected List<String> getAllQueryTerms() {
		return list("Antony", "Brutus");
	}
}