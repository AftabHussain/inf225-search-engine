package edu.uci.ics.inf225.searchengine.search.scoring.solvers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.CosineSimilarityBuilder;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.ScoringUtils;

public class CosineSimilarityQueryRanker implements ScoringContributor {

	@Override
	public void score(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, TermIndex termIndex, DocumentIndex docIndex, QueryScorer queryScorer) {
		Map<String, Integer> queryCardinalityMap = CollectionUtils.getCardinalityMap(allQueryTerms);

		Map<Integer, CosineSimilarityBuilder> cosSimPerDoc = new HashMap<>();

		double queryEuclideanLength = 0d;

		for (Entry<String, PostingsList> postingList : postingsLists.entrySet()) {
			double queryTermTFIDF = ScoringUtils.tfidf(queryCardinalityMap.get(postingList.getKey()), docIndex.count(), postingList.getValue().size());

			queryEuclideanLength += Math.pow(queryTermTFIDF, 2);

			Iterator<Posting> postingsIterator = postingList.getValue().iterator();

			while (postingsIterator.hasNext()) {
				Posting eachPosting = postingsIterator.next();
				CosineSimilarityBuilder builder = cosSimPerDoc.get(eachPosting.getDocID());

				if (builder == null) {
					builder = new CosineSimilarityBuilder();
					cosSimPerDoc.put(eachPosting.getDocID(), builder);
				}
				builder.addWeights(eachPosting.getTfidf(), queryTermTFIDF);
			}
		}
		queryEuclideanLength = Math.sqrt(queryEuclideanLength);

		for (Entry<Integer, CosineSimilarityBuilder> entry : cosSimPerDoc.entrySet()) {
			entry.getValue().calculate(docIndex.getDoc(entry.getKey()).getEuclideanLength(), queryEuclideanLength);
			queryScorer.getScorer(entry.getKey()).setTextCosineSimilarity(entry.getValue().getCachedCosineSimilary());
		}
	}
}
