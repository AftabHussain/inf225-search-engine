package edu.uci.ics.inf225.searchengine.search.solvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;

public class TFIDFQueryRanker implements QueryRanker {

	@Override
	public List<Integer> query(List<String> allQueryTerms, Map<String, PostingsList> postingsLists, int limit, TermIndex termIndex, DocumentIndex docIndex) {
		List<Integer> docIDs_final = new ArrayList<>(limit);
		List<Integer> docIDs = new ArrayList<>();
		List<Float> tfIdfs = new ArrayList<>();
		Map docID_map = new HashMap();

		for (PostingsList postingList : postingsLists.values()) {

			Iterator<Posting> postingsIterator = postingList.iterator();

			while (postingsIterator.hasNext()) {
				Posting posting = postingsIterator.next();
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
		for (int j = 0; j < limit && iter.hasNext(); j++) {
			Map.Entry mEntry = (Map.Entry) iter.next();
			docIDs_final.add(Integer.parseInt(mEntry.getKey().toString()));
		}
		return docIDs_final;
	}

}
