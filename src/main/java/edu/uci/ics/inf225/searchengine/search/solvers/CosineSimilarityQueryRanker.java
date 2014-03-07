package edu.uci.ics.inf225.searchengine.search.solvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import edu.uci.ics.inf225.searchengine.search.scoring.ScoringUtils;

public class CosineSimilarityQueryRanker implements QueryRanker {

	@Override
	public List<Integer> query(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, int limit, TermIndex termIndex, DocumentIndex docIndex) {
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
		}

		List<Entry<Integer, CosineSimilarityBuilder>> rankedEntries = new ArrayList<>(cosSimPerDoc.entrySet());

		Collections.sort(rankedEntries, new Comparator<Entry<Integer, CosineSimilarityBuilder>>() {

			@Override
			public int compare(Entry<Integer, CosineSimilarityBuilder> o1, Entry<Integer, CosineSimilarityBuilder> o2) {
				if (o1.getValue().getCachedCosineSimilary() > o2.getValue().getCachedCosineSimilary()) {
					return 1;
				} else if ((o1.getValue().getCachedCosineSimilary() == o2.getValue().getCachedCosineSimilary())) {
					return 0;
				} else {
					return -1;
				}
			}

		});

		List<Integer> rankedDocs = new ArrayList<>(limit);

		int i = 0;
		for (Entry<Integer, CosineSimilarityBuilder> entry : rankedEntries) {
			if (i < limit) {
				rankedDocs.add(entry.getKey());
				i++;
			}
		}

		return rankedDocs;
	}
}
