package edu.uci.ics.inf225.searchengine.search.solvers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class CosineSimilarityQueryRankingTest extends QueryRankerTest {

	@Override
	protected QueryRanker createQueryRanker() {
		return new CosineSimilarityQueryRanker();
	}

	@Override
	protected List<Integer> expectedRankingDocs() {
		// Query = Q: "Antony Brutus"
		// ED(d) = Euclidean Distance of d.
		// DP(d1,d2) = Dot Product of d1 and d2.
		// CS(d1,d2) = Cosine Similarity between d1 and d2.

		// TFIDF("Antony", q) = 0.477121255
		// TFIDF("Brutus", q) = 0.301029996

		// ED(q) = sqrt(0.477121255 ^ 2 + 0.301029996 ^ 2) = 0.564148695
		// ED(d1) = 22.38
		// ED(d2) = 18.15
		// ED(d3) = 0.92
		// ED(d4) = 1.55
		// ED(d5) = 1.12
		// ED(d6) = 0.42

		// DP(d1,q) = sqrt(13.1 * 0.477121255 + 3.0 * 0.301029996) = 2.674580047
		// DP(d2,q) = sqrt(11.4 * 0.477121255) = 2.33220546
		// DP(d3,q) = 0
		// DP(d4,q) = sqrt(1.0 * 0.301029996) = 0.548662005
		// DP(d5,q) = 0
		// DP(d6,q) = 0

		// CS(d1,q) = DP(d1,q)/(ED(d1)*ED(q))=2.674580047/(22.38*0.564148695)=0.211837055
		// CS(d2,q) = DP(d2,q)/(ED(d2)*ED(q))=2.33220546/(18.15*0.564148695)=0.227770036
		// CS(d3,q) = DP(d3,q)/(ED(d3)*ED(q))=0
		// CS(d4,q) = DP(d4,q)/(ED(d4)*ED(q))=0.548662005/(1.55*0.564148695)=0.627450689
		// CS(d5,q) = DP(d5,q)/(ED(d5)*ED(q))=0
		// CS(d6,q) = DP(d6,q)/(ED(d6)*ED(q))=0

		return Arrays.asList(new Integer[] { 4, 2, 1 });
	}

	@Override
	protected TermIndex getTermIndex() {
		TermIndex termIndex = mock(TermIndex.class);

		when(termIndex.idf("Antony")).thenReturn((double) 6 / (double) 2);
		when(termIndex.idf("Brutus")).thenReturn((double) 6 / (double) 3);

		return termIndex;
	}

	@Override
	protected DocumentIndex getDocIndex() {
		DocumentIndex docIndex = mock(DocumentIndex.class);

		when(docIndex.count()).thenReturn(6);

		when(docIndex.getDoc(1)).thenReturn(webPage(22.38d));
		when(docIndex.getDoc(2)).thenReturn(webPage(18.15d));
		when(docIndex.getDoc(3)).thenReturn(webPage(0.92d));
		when(docIndex.getDoc(4)).thenReturn(webPage(1.55d));
		when(docIndex.getDoc(5)).thenReturn(webPage(1.12d));
		when(docIndex.getDoc(6)).thenReturn(webPage(0.42d));

		return docIndex;
	}

	private static WebPage webPage(double euclideanLength) {
		WebPage page = new WebPage();
		page.setEuclideanLength((float) euclideanLength);
		return page;
	}

	@Override
	protected List<String> getAllQueryTerms() {
		return list("Antony", "Brutus");
	}
}