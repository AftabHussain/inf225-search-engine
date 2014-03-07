package edu.uci.ics.inf225.searchengine.search.solvers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class CosineSimilarityQueryRankingTest extends QueryRankerTest {

	@Override
	protected QueryRanker createQueryRanker() {
		return new CosineSimilarityQueryRanker();
	}

	@Override
	protected List<Integer> expectedRankingDocs() {
		// tfidf("TFIDF", d1) = 1.2f
		// tfidf("TFIDF", d2) = 3.4f
		// tfidf("Information", d1) = 0.4f
		// tfidf("Information", d3) = 1.1f
		// tfidf("Retrieval", d2) = 0.9f
		// tfidf("Retrieval", d4) = 1.3f
		// tfidf("Retrieval", d5) = 10.0f

		// Query = Q: "TFIDF Information Retrieval TFIDF"
		// ED(d) = Euclidean Distance of d.
		// DP(d1,d2) = Dot Product of d1 and d2.
		// CS(d1,d2) = Cosine Similarity between d1 and d2.

		// ED(q) = sqrt(2^2 + 1^2 + 1^2) = sqrt(6)
		// ED(d1) = sqrt(1.2^2 + 0.4f^2) = sqrt(1.84)
		// ED(d2) = sqrt(3.4^2 + 0.9f^2) = sqrt(12.37)
		// ED(d3) = sqrt(1.1^2) = 1.1
		// ED(d4) = sqrt(1.3^2) = 1.3
		// ED(d5) = sqrt(10.0^2) = 10.0

		// DP(d1,q) = sqrt(1.2 * 2 + 0.4 * 1) = sqrt(2.8)
		// DP(d2,q) = sqrt(3.4 * 2 + 0.9 * 1) = sqrt(7.7)
		// DP(d3,q) = sqrt(1.1 * 1) = sqrt(1.1)
		// DP(d4,q) = sqrt(1.3 * 1) = sqrt(1.3)
		// DP(d5,q) = sqrt(10.0 * 1) = sqrt(10.0)

		// CS(d1,q) = DP(d1,q)/(ED(d1)*ED(q))=sqrt(2.8)/(sqrt(1.6)*sqrt(6))
		// =0.540061725
		// CS(d2,q) = DP(d2,q)/(ED(d2)*ED(q))=sqrt(7.7)/(sqrt(12.37)*sqrt(6))
		// =0.322095671
		// CS(d3,q) = DP(d3,q)/(ED(d3)*ED(q))=sqrt(1.1)/(1.1*sqrt(6))
		// =0.389249472
		// CS(d4,q) = DP(d4,q)/(ED(d4)*ED(q))=sqrt(1.3)/(1.3*sqrt(6))
		// =0.358057437
		// CS(d5,q) = DP(d5,q)/(ED(d5)*ED(q))=sqrt(10.0)/(10.0*sqrt(6))
		// =0.129099445

		return Arrays.asList(new Integer[] { 1, 3, 4, 2, 5 });
	}

	@Override
	protected DocumentIndex getDocIndex() {
		DocumentIndex docIndex = mock(DocumentIndex.class);

		when(docIndex.getDoc(1)).thenReturn(webPage(Math.sqrt(1.6d)));
		when(docIndex.getDoc(2)).thenReturn(webPage(Math.sqrt(12.37d)));
		when(docIndex.getDoc(3)).thenReturn(webPage(1.1d));
		when(docIndex.getDoc(4)).thenReturn(webPage(1.3d));
		when(docIndex.getDoc(5)).thenReturn(webPage(10.0d));

		return docIndex;
	}

	private static WebPage webPage(double euclideanLength) {
		WebPage page = new WebPage();
		page.setEuclideanLength((float) euclideanLength);
		return page;
	}
}