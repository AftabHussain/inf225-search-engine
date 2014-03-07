package edu.uci.ics.inf225.searchengine.search.solvers;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public abstract class QueryRankerTest {

	private QueryRanker queryRanker;

	private final List<String> allQueryTerms = list("TFIDF", "Information", "Retrieval", "TFIDF");

	@Before
	public void setUp() throws Exception {
		queryRanker = createQueryRanker();

	}

	protected abstract List<Integer> expectedRankingDocs();

	@Test
	public void testRanking() {
		Map<String, PostingsList> postingsLists = new HashMap<>();
		postingsLists.put("TFIDF", postingsList(posting(1, 1, 1.2f), posting(2, 3, 3.4f)));
		postingsLists.put("Information", postingsList(posting(1, 1, 0.4f), posting(3, 5, 1.1f)));
		postingsLists.put("Retrieval", postingsList(posting(2, 1, 0.9f), posting(4, 3, 1.3f), posting(5, 1, 10.0f)));
		List<Integer> rankedDocs = getQueryRanker().query(allQueryTerms, postingsLists, 5, mock(TermIndex.class), getDocIndex());

		List<Integer> expectedRankingDocs = expectedRankingDocs();

		for (int i = 0; i < rankedDocs.size(); i++) {
			System.out.println("Checking Doc " + (i + 1) + ". Expected=[" + expectedRankingDocs.get(i) + "] Obtained=[" + rankedDocs.get(i) + "]");
			Assert.assertEquals("Wrong TF-IDF Ranking for Doc " + (i + 1), expectedRankingDocs.get(i), rankedDocs.get(i));
		}
	}

	protected DocumentIndex getDocIndex() {
		return mock(DocumentIndex.class);
	}

	protected QueryRanker getQueryRanker() {
		return queryRanker;
	}

	protected abstract QueryRanker createQueryRanker();

	protected static Posting posting(int docID, int tf, float tfidf) {
		Integer[] positions = new Integer[tf];
		Arrays.fill(positions, 1);
		Posting posting = new Posting(docID, tf);
		posting.setTfidf(tfidf);
		return posting;
	}

	protected static PostingsList postingsList(Posting... postings) {
		PostingsList plist = new PostingsList();

		for (int i = 0; i < postings.length; i++) {
			plist.addPosting(postings[i]);
		}

		return plist;
	}

	protected static List<String> list(String... words) {
		return new ArrayList<>(Arrays.asList(words));
	}
}