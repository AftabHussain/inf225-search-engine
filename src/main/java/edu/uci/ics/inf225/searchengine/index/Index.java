package edu.uci.ics.inf225.searchengine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Index {

	private Map<String, Integer> docCount = new HashMap<String, Integer>();
	private Map<String, Map<String, TermInDoc>> termsMap = new HashMap<String, Map<String, TermInDoc>>();

	public Index() {
	}

	public Map<String, TermInDoc> docsForTerm(String term) {
		if (termsMap.containsKey(term)) {
			return termsMap.get(term);
		} else {
			return new HashMap<>(1);
		}
	}

	/**
	 * 
	 * @return The number of unique documents indexed.
	 */
	public int numberOfDocuments() {
		return this.docCount.size();
	}

	/**
	 * Retrieves the number of terms appearing in this document.
	 * 
	 * @param doc
	 *            The document identifier.
	 * @return The number of terms in the document.
	 */
	public int docFrequency(String doc) {
		return this.docCount.get(doc);
	}

	public void indexTerm(String term, String url, int position) {
		Map<String, TermInDoc> termPosting;

		// This Hashmap is for keeping the number of total documents
		if (docCount.containsKey(url)) {
			int DocFrequency = docCount.get(url);
			docCount.put(url, DocFrequency + 1);
		} else {
			docCount.put(url, 1);
		}

		if (termsMap.containsKey(term)) {// we had inserted the token before
			termPosting = new HashMap<String, TermInDoc>();
			termPosting = termsMap.get(term);
			if (termPosting.containsKey(url)) { // we had inserted the document
												// before for this term
				TermInDoc termInDoc = termPosting.get(url);
				termInDoc.addPosition(position);
				termInDoc.settermFrequency(termInDoc.gettermFrequency() + 1);
				termPosting.put(url, termInDoc);
			} else { // we had not inserted the document before for this term
				List<Integer> positions = new ArrayList<Integer>();
				positions.add(position);
				TermInDoc docmap = new TermInDoc(url, 1, positions);
				termPosting.put(url, docmap);
			}
		} else {
			List<Integer> positions = new ArrayList<Integer>();
			positions.add(position);
			termPosting = new HashMap<String, TermInDoc>();
			termPosting.put(url, new TermInDoc(url, 1, positions));
			termsMap.put(term, termPosting);
		}
	}

	public void postProcess() {

		int N = this.docCount.size();

		Iterator<Entry<String, Map<String, TermInDoc>>> it = this.termsMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Map<String, TermInDoc>> outerEntry = it.next();
			String term = (String) outerEntry.getKey();
			Map<String, TermInDoc> innerMap = outerEntry.getValue();
			Integer docsSize = innerMap.size();
			Iterator<Entry<String, TermInDoc>> innerIt = innerMap.entrySet().iterator();
			while (innerIt.hasNext()) {
				Entry<String, TermInDoc> innerEntry = innerIt.next();

				String doc = (String) innerEntry.getKey();
				TermInDoc termList = (TermInDoc) innerEntry.getValue();
				termList.settfidf((float) (termList.gettermFrequency() * Math.log(N / docsSize)));
				innerIt.remove();
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void sort() {
		termsMap = new TreeMap<String, Map<String, TermInDoc>>(termsMap);

		Iterator<Entry<String, Map<String, TermInDoc>>> it = termsMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<String, TermInDoc>> outerEntry = it.next();
			Map<String, TermInDoc> innerMap = outerEntry.getValue();
			DocComparator bvc = new DocComparator(innerMap);
			TreeMap<String, TermInDoc> sorted_map = new TreeMap<String, TermInDoc>(bvc);
			sorted_map.putAll(innerMap);
			outerEntry.setValue(sorted_map);
		}
	}
}