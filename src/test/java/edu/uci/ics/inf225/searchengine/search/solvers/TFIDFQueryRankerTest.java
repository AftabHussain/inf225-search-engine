package edu.uci.ics.inf225.searchengine.search.solvers;

import org.junit.Test;

public class TFIDFQueryRankerTest extends QueryRankerTest {

	@Override
	protected QueryRanker createQueryRanker() {
		return new TFIDFQueryRanker();
	}

	@Test
	public void testTFIDFRanking() {
		// 1: 13.1 + 3.0 = 16.1
		// 2: 11.4 + 8.3 = 19.7
		// 3: 0
		// 4: 1.0 = 1.0
		// 5:
		// 6:
		this.runQuery(getTestPostingsLists(), list("Antony", "Brutus"), list(2, 1, 4));
	}
}