package edu.uci.ics.inf225.searchengine.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.solvers.CosineSimilarityQueryRanker;
import edu.uci.ics.inf225.searchengine.search.solvers.QueryRanker;
import edu.uci.ics.inf225.searchengine.search.solvers.TFIDFQueryRanker;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class BasicSearchEngine implements SearchEngine {

	private DocumentIndex docIndex;

	private TermIndex termIndex;

	private TextTokenizer tokenizer;

	private QueryRanker singleTermQueryRanker;

	private QueryRanker multiTermQueryRanker;

	public BasicSearchEngine() {
		tokenizer = createTokenizer();
		singleTermQueryRanker = new TFIDFQueryRanker();
		multiTermQueryRanker = new CosineSimilarityQueryRanker();
	}

	public void start(String indexFilename) throws IOException {
		readIndexFromDisk(indexFilename);
		tokenizer.start();
	}

	public void start() throws IOException {
		start(Indexer.INDEX_FILENAME);
	}

	private TextTokenizer createTokenizer() {
		return new TextTokenizer();
	}

	public void shutdown() {
		tokenizer.stop();
		docIndex.shutdown();
	}

	private void readIndexFromDisk(String filename) throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));

		try {
			docIndex = (DocumentIndex) inputStream.readObject();
			termIndex = (TermIndex) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
	}

	private List<Integer> rankWithTFIDF(Map<String, List<Posting>> postingsMap, int num) {
		List<Integer> docIDs_final = new ArrayList<>(num);
		List<Integer> docIDs = new ArrayList<>();
		List<Float> tfIdfs = new ArrayList<>();
		Map docID_map = new HashMap();

		for (List<Posting> postings : postingsMap.values()) {
			for (Posting posting : postings) {
				if (docIDs.contains(posting.getDocID())) {
					int marked_idx = docIDs.indexOf(posting.getDocID());
					float current_tfidf = tfIdfs.get(marked_idx);
					float new_tfidf = current_tfidf + posting.getTfidf();
					tfIdfs.set(marked_idx, new_tfidf);
				} else {
					docIDs.add(posting.getDocID());
					tfIdfs.add(posting.getTfidf());
				}
			}
		}

		for (int i = 0; i < docIDs.size(); i++) {
			docID_map.put(docIDs.get(i), tfIdfs.get(i));
		}

		List list = new LinkedList(docID_map.entrySet());

		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		// put sorted list into map again
		// LinkedHashMap preserves order in which keys were inserted
		String[] arr1 = new String[list.size()], arr2 = new String[list.size()];
		int i = 0;

		// Map freqSorted = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			// freqSorted.put(entry.getKey(), entry.getValue());
			arr1[i] = entry.getKey().toString();
			arr2[i] = entry.getValue().toString();
			i++;
		}

		// obtaining the list in descending order
		Map docID_mapD = new LinkedHashMap();
		for (int j = arr1.length - 1; j > -1; j--) {
			docID_mapD.put(arr1[j], arr2[j]);
		}

		Iterator iter = docID_mapD.entrySet().iterator();
		for (int j = 0; j < num && iter.hasNext(); j++) {
			Map.Entry mEntry = (Map.Entry) iter.next();
			docIDs_final.add(Integer.parseInt(mEntry.getKey().toString()));
		}
		return docIDs_final;
	}

	public QueryResult query(String query) throws QueryException {
		if (query.trim().isEmpty()) {
			throw new QueryException("Query cannot be empty.");
		}
		long start = System.nanoTime();
		PageTokenStream stream = null;
		try {
			stream = tokenizer.tokenize(query);

			Map<String, PostingsList> postings = new HashMap<>();

			List<String> allQueryTerms = new LinkedList<>();

			while (stream.increment()) {
				PageToken token = stream.next();
				allQueryTerms.add(token.getTerm());
			}

			for (String queryTerm : allQueryTerms) {
				if (!postings.containsKey(queryTerm)) {
					/*
					 * Process only if this query term has not been processed
					 * before.
					 */
					postings.put(queryTerm, this.termIndex.postingsList(queryTerm));
				}
			}

			List<Integer> rankedPostings;
			QueryRanker queryRanker = null;
			if (postings.size() == 1) {
				// rankedPostings = rankWithTFIDF(postings, 10);
				queryRanker = this.singleTermQueryRanker;
			} else {
				// rankedPostings = rankWithCosineSimilarity(allQueryTerms,
				// postings, 10);
				queryRanker = this.multiTermQueryRanker;
			}
			rankedPostings = queryRanker.query(allQueryTerms, postings, 10, termIndex, docIndex);

			QueryResult queryResult = createQueryResult(rankedPostings);
			queryResult.setTotalPages(postings.size());
			queryResult.setExecutionTime(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
			return queryResult;
		} catch (IOException e) {
			throw new QueryException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new QueryException(e);
			}
		}
	}

	private List<Integer> rankWithCosineSimilarity(List<String> allQueryTerms, Map<String, PostingsList> postings, int n) {
		double queryEuclideanLength = 0f;
		Map<String, Integer> queryCardinalityMap = CollectionUtils.getCardinalityMap(allQueryTerms);

		Map<Integer, Double> cosSimPerDoc = new HashMap<>();

		for (Entry<String, PostingsList> queryTerm : postings.entrySet()) {
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

		List<Integer> rankedDocs = new ArrayList<>(n);

		int i = 0;
		for (Entry<Integer, Double> entry : rankedEntries) {
			if (i < n) {
				rankedDocs.add(entry.getKey());
				i++;
			}
		}

		return rankedDocs;
	}

	private QueryResult createQueryResult(List<Integer> rank) {
		QueryResult queryResult = new QueryResult(rank.size());

		for (Integer docID : rank) {
			queryResult.addEntry(createQueryResultEntry(docID));
		}

		return queryResult;
	}

	private QueryResultEntry createQueryResultEntry(Integer docID) {
		QueryResultEntry entry = new QueryResultEntry();
		WebPage page = docIndex.getDoc(docID);
		entry.setUrl(page.getUrl());
		entry.setTitle(page.getTitle());
		entry.setContent(page.getContent());

		return entry;
	}
}