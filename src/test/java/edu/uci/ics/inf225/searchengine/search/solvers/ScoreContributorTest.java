package edu.uci.ics.inf225.searchengine.search.solvers;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.ScoringContributor;

/**
 * This test case is an abstract test definition for different rankers to solve
 * it. It defines some base data and a query, different rankers will have to
 * test that the obtained ranked is the same as the expected one.
 * 
 */
public abstract class ScoreContributorTest {

	private ScoringContributor scoreContributor;

	@Before
	public void setUp() throws Exception {
		scoreContributor = createScoreContributor();
	}

	protected void runQuery(Map<String, PostingsList> postingsLists, List<String> allQueryTerms, Map<Integer, Double> expectedScores) {
		/*
		 * Remove the unrelated postings.
		 */
		Set<String> allKeys = new HashSet<>(postingsLists.keySet());
		allKeys.removeAll(allQueryTerms);

		for (String key : allKeys) {
			postingsLists.remove(key);
		}

		QueryScorer queryScorer = new QueryScorer();

		getQueryRanker().score(allQueryTerms, postingsLists, getTermIndex(), getDocIndex(), queryScorer);

		Assert.assertEquals("The number of scores is different from the expected one", expectedScores.size(), queryScorer.count());

		for (Entry<Integer, Double> expectedDocScore : expectedScores.entrySet()) {
			Assert.assertEquals("Score for Doc#" + expectedDocScore.getKey() + " is wrong.", expectedDocScore.getValue(), queryScorer.getScorer(expectedDocScore.getKey()).getTextCosineSimilarity(),
					0.01d);
		}
	}

	// protected int id(String term) {

	// }

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

	protected ScoringContributor getQueryRanker() {
		return scoreContributor;
	}

	protected abstract ScoringContributor createScoreContributor();

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