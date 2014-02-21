package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Index {

	private Map<String, Integer> DocCount = new HashMap<String, Integer>();
	private Map<String, Map<String, DocMap>> termsMap = new HashMap<String, Map<String, DocMap>>();

	public void indexTerm(String term, String url, int position) {
		// TODO To be implemented.
		Map<String, DocMap> termPosting;

		// This Hashmap is for keeping the number of total documents
		if (DocCount.containsKey(url)) {
			int DocFrequency = DocCount.get(url);
			DocCount.put("URL", DocFrequency + 1);
		} else {
			DocCount.put(url, 1);
		}

		if (termsMap.containsKey(term)) {// we had inserted the token before
			termPosting = new HashMap<String, DocMap>();
			termPosting = termsMap.get(term);
			if (termPosting.containsKey(url)) { // we had inserted the document
												// before for this term
				DocMap docmap = termPosting.get(url);
				docmap.positions.add(position);
				docmap.settermFrequency(docmap.gettermFrequency() + 1);
				termPosting.put(url, docmap);
			} else { // we had not inserted the document before for this term
				List<Integer> positions = new ArrayList<Integer>();
				positions.add(position);
				DocMap docmap = new DocMap(1, positions);
				termPosting.put(url, docmap);
				// termsMap.put(,termPosting);
			}
		} else {
			List<Integer> positions = new ArrayList<Integer>();
			positions.add(position);
			termPosting = new HashMap<String, DocMap>();
			termPosting.put(url, new DocMap(1, positions));
			termsMap.put(term, termPosting);
		}
	}

	public Integer getNumberofAlldocuments() {
		return this.DocCount.size();
	}

	public void postProcess() {

		int N = this.DocCount.size();

		Iterator it = this.termsMap.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry outerEntry = (Map.Entry) it.next();
			// System.out.println(outerEntry.getKey() + " = " +
			// outerEntry.getValue());
			String term = (String) outerEntry.getKey();
			Map<String, DocMap> innerMap = (Map) outerEntry.getValue();
			Integer docsSize = innerMap.size();
			// System.out.println (docsSize);
			Iterator innerIt = innerMap.entrySet().iterator();
			while (innerIt.hasNext()) {
				Map.Entry innerEntry = (Map.Entry) innerIt.next();

				String doc = (String) innerEntry.getKey();
				DocMap termList = (DocMap) innerEntry.getValue();
				// termList.settfidf(12);
				System.out.println(termList.gettermFrequency() + " , " + N
						/ docsSize);
				termList.settfidf((float) (termList.gettermFrequency() * Math
						.log(N / docsSize)));
				System.out.println(innerEntry.getKey() + " = "
						+ innerEntry.getValue());
				innerIt.remove();
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void sort() {
		termsMap = new TreeMap<String, Map<String, DocMap>>(termsMap);

		Iterator<Entry<String, Map<String, DocMap>>> it = termsMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, Map<String, DocMap>> outerEntry = it.next();
			Map<String, DocMap> innerMap = outerEntry.getValue();
			docComparator bvc = new docComparator(innerMap);
			TreeMap<String, DocMap> sorted_map = new TreeMap<String, DocMap>(
					bvc);
			sorted_map.putAll(innerMap);
			outerEntry.setValue(sorted_map);
		}
	}
}
