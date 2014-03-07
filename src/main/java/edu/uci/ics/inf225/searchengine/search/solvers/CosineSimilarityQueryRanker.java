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

public class CosineSimilarityQueryRanker implements QueryRanker {

	@Override
	public List<Integer> query(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, int limit, TermIndex termIndex, DocumentIndex docIndex) {
		double queryEuclideanLength = 0f;
		Map<String, Integer> queryCardinalityMap = CollectionUtils.getCardinalityMap(allQueryTerms);

		Map<Integer, Double> cosSimPerDoc = new HashMap<>();

		for (Entry<String, PostingsList> queryTerm : postingsLists.entrySet()) {
			double queryTermWeight = queryCardinalityMap.get(queryTerm.getKey());
			queryEuclideanLength += Math.pow(queryTermWeight, 2d);

			Iterator<Posting> postingsIterator = queryTerm.getValue().iterator();

			while (postingsIterator.hasNext()) {
				Posting eachPosting = postingsIterator.next();
				if (cosSimPerDoc.containsKey(eachPosting.getDocID())) {
					cosSimPerDoc.put(eachPosting.getDocID(), cosSimPerDoc.get(eachPosting.getDocID()) + eachPosting.getTfidf() * queryTermWeight);

				} else {
					cosSimPerDoc.put(eachPosting.getDocID(), eachPosting.getTfidf() * queryTermWeight);
				}
			}
		}

		queryEuclideanLength = Math.sqrt(queryEuclideanLength);
		for (Entry<Integer, Double> entry : cosSimPerDoc.entrySet()) {
			entry.setValue(Math.sqrt(entry.getValue()) / (docIndex.getDoc(entry.getKey()).getEuclideanLength() * queryEuclideanLength));
		}

		List<Entry<Integer, Double>> rankedEntries = new ArrayList<>(cosSimPerDoc.entrySet());

		Collections.sort(rankedEntries, new Comparator<Entry<Integer, Double>>() {

			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return (int) (o2.getValue() - o1.getValue());
			}
		});

		List<Integer> rankedDocs = new ArrayList<>(limit);

		int i = 0;
		for (Entry<Integer, Double> entry : rankedEntries) {
			if (i < limit) {
				rankedDocs.add(entry.getKey());
				i++;
			}
		}

		return rankedDocs;
	}

}
