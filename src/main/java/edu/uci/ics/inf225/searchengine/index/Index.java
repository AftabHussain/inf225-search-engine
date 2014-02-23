package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javolution.util.FastMap;
import javolution.util.function.Equalities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index implements Externalizable {

	private static final Logger log = LoggerFactory.getLogger(Index.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private Map<String, Integer> docCount = new HashMap<String, Integer>();
	// private Map<Long, String> docIDs = new HashMap<>(100000);
	private Map<Long, String> docIDs = new FastMap<>();
	// private Map<String, Map<Long, TermInDoc>> termsMap = new HashMap<String,
	// Map<Long, TermInDoc>>(750000);

	private Map<String, Map<Long, TermInDoc>> termsMap = new FastMap<>(Equalities.LEXICAL_FAST);

	public Index() {
	}

	public Map<Long, TermInDoc> docsForTerm(String term) {
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
		// return this.docCount.size();
		return this.docIDs.size();
	}

	public long numberOfUniqueTerms() {
		return this.termsMap.keySet().size();
	}

	/**
	 * Retrieves the number of terms appearing in this document.
	 * 
	 * @param doc
	 *            The document identifier.
	 * @return The number of terms in the document.
	 */
	public int docFrequency(String doc) {
		// return this.docCount.get(doc);
		return 0; // FIXME
	}

	public void newDocument(String url, long docID) {
		this.docIDs.put(docID, url);
	}

	public void indexDoc(long docID, List<String> terms, List<Integer> positions) {
		if (terms.size() != positions.size()) {
			throw new IllegalArgumentException();
		}

		Iterator<String> termsIterator = terms.iterator();
		Iterator<Integer> positionIterator = positions.iterator();

		while (termsIterator.hasNext()) {
			this.indexTerm(docID, termsIterator.next(), positionIterator.next());
		}
	}

	public void indexTerm(long docID, String term, int position) {

		Map<Long, TermInDoc> termPosting;
		TermInDoc termInDoc;

		synchronized (this) {
			termPosting = termsMap.get(term);

			if (termPosting == null) {
				termPosting = createEmptyPosting();
				termsMap.put(term, termPosting);
			}

			termInDoc = termPosting.get(docID);

			if (termInDoc == null) {
				termInDoc = createEmptyTermInDoc(docID);
				termPosting.put(docID, termInDoc);
			}
			termInDoc.addPosition(position);
		}

		termInDoc.increaseTF();
	}

	private TermInDoc createEmptyTermInDoc(long docID) {
		return new TermInDoc(docID, 0, new LinkedList<Integer>());
	}

	private Map<Long, TermInDoc> createEmptyPosting() {
		// return new HashMap<Long, TermInDoc>();
		return new FastMap<Long, TermInDoc>();
	}

	public void postProcess() {

		int N = this.numberOfDocuments();

		Iterator<Entry<String, Map<Long, TermInDoc>>> it = this.termsMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Map<Long, TermInDoc>> outerEntry = it.next();
			String term = (String) outerEntry.getKey();
			Map<Long, TermInDoc> innerMap = outerEntry.getValue();
			Integer docsSize = innerMap.size();
			Iterator<Entry<Long, TermInDoc>> innerIt = innerMap.entrySet().iterator();
			while (innerIt.hasNext()) {
				Entry<Long, TermInDoc> innerEntry = innerIt.next();

				TermInDoc termList = (TermInDoc) innerEntry.getValue();
				termList.setTfidf((float) (termList.getTf() * Math.log(N / docsSize)));
				innerIt.remove();
			}
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void sort() {
		termsMap = new TreeMap<String, Map<Long, TermInDoc>>(termsMap);

		Iterator<Entry<String, Map<Long, TermInDoc>>> it = termsMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Map<Long, TermInDoc>> outerEntry = it.next();
			Map<Long, TermInDoc> innerMap = outerEntry.getValue();
			DocComparator bvc = new DocComparator(innerMap);
			TreeMap<Long, TermInDoc> sorted_map = new TreeMap<Long, TermInDoc>(bvc);
			sorted_map.putAll(innerMap);
			outerEntry.setValue(sorted_map);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(docIDs);
		out.flush();
		int i = 0;
		out.writeInt(termsMap.size());
		for (Entry<String, Map<Long, TermInDoc>> entry : termsMap.entrySet()) {
			out.writeUTF(entry.getKey());
			out.writeObject(entry.getValue());
			i++;

			if (i == 50000) {
				out.flush();
				i = 0;
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}
}