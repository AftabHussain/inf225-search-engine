package edu.uci.ics.inf225.searchengine.search.solvers;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

/**
 * This test case is an abstract test definition for different rankers to solve
 * it. It defines some base data and a query, different rankers will have to
 * test that the obtained ranked is the same as the expected one.
 * 
 */
public abstract class QueryRankerTest {

	private QueryRanker queryRanker;

	@Before
	public void setUp() throws Exception {
		queryRanker = createQueryRanker();

	}

	protected void runQuery(Map<String, PostingsList> postingsLists, List<String> allQueryTerms, List<Integer> expectedRankingDocs) {
		/*
		 * Remove the unrelated postings.
		 */
		Set<String> allKeys = new HashSet<>(postingsLists.keySet());
		allKeys.removeAll(allQueryTerms);

		for (String key : allKeys) {
			postingsLists.remove(key);
		}

		List<Integer> rankedDocs = getQueryRanker().query(allQueryTerms, postingsLists, expectedRankingDocs.size(), getTermIndex(), getDocIndex());

		for (int i = 0; i < rankedDocs.size(); i++) {
			System.out.println("Checking Doc " + (i + 1) + ". Expected=[" + expectedRankingDocs.get(i) + "] Obtained=[" + rankedDocs.get(i) + "]");
			Assert.assertEquals("Wrong Ranking for Doc " + (i + 1), expectedRankingDocs.get(i), rankedDocs.get(i));
		}
	}

	protected Map<String, PostingsList> getTestPostingsLists() {
		/*
		 * DATA DEFINITION (from LUCILab slides)
		 * 
		 * 6 Documents: Antony and Cleopatra (id=1), Juluis Caesar (id=2), The
		 * Tempest (id=3), Hamlet (id=4), Othello (id=5), Macbeth (id=6). 7
		 * Terms: Antony, Brutus, Caesar, Calpurnia, Cleopatra, mercy, worser.
		 */
		Map<String, PostingsList> postingsLists = new HashMap<>();
		postingsLists.put("Antony", postingsList(posting(1, 1, 13.1f), posting(2, 1, 11.4f)));
		postingsLists.put("Brutus", postingsList(posting(1, 1, 3.0f), posting(2, 1, 8.3f), posting(4, 1, 1.0f)));
		postingsLists.put("Caesar", postingsList(posting(1, 1, 2.3f), posting(2, 1, 2.3f), posting(4, 1, 0.5f), posting(5, 1, 0.3f), posting(6, 1, 0.3f)));
		postingsLists.put("Calpurnia", postingsList(posting(2, 1, 11.2f)));
		postingsLists.put("Cleopatra", postingsList(posting(1, 1, 17.7f)));
		postingsLists.put("mercy", postingsList(posting(1, 1, 0.5f), posting(3, 1, 0.7f), posting(4, 1, 0.9f), posting(5, 1, 0.9f), posting(6, 1, 0.3f)));
		postingsLists.put("worser", postingsList(posting(1, 1, 1.2f), posting(3, 1, 0.6f), posting(4, 1, 0.6f), posting(5, 1, 0.6f)));
		return postingsLists;
	}

	protected TermIndex getTermIndex() {
		return mock(TermIndex.class);
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

	@SafeVarargs
	protected static <T> List<T> list(T... words) {
		return new ArrayList<>(Arrays.asList(words));
	}
}