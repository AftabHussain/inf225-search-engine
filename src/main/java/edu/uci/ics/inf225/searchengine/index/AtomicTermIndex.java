package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.index.postings.TFIDFPostingComparator;
import edu.uci.ics.inf225.searchengine.utils.MapUtils;

public class AtomicTermIndex implements TermIndex, Externalizable {

	private static final Logger log = LoggerFactory.getLogger(AtomicTermIndex.class);

	private static final long serialVersionUID = 1L;

	private Map<String, PostingsList> termsMap;

	private HashMap<String, PostingsList> createTermsMap(int initialCapacity) {
		return new HashMap<>(initialCapacity);
	}

	public AtomicTermIndex() {
		this(3500000);
	}

	public AtomicTermIndex(int initialCapacity) {
		termsMap = createTermsMap(initialCapacity);
	}

	private Posting createEmptyTermInDoc(int docID) {
		return new Posting(docID, 0, new LinkedList<Integer>());
	}

	private PostingsList createEmptyPostingsList() {
		return new PostingsList();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(termsMap.size());
		for (Entry<String, PostingsList> entry : termsMap.entrySet()) {
			out.writeUTF(entry.getKey());
			out.writeObject(entry.getValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		termsMap = createTermsMap(size);

		for (int i = 0; i < size; i++) {
			termsMap.put(in.readUTF(), (PostingsList) in.readObject());
		}
	}

	@Override
	public void newTerm(int docID, String term, int position) {
		PostingsList postingsList;
		Posting posting;

		postingsList = termsMap.get(term);

		if (postingsList == null) {
			postingsList = createEmptyPostingsList();
			termsMap.put(term, postingsList);
		}

		posting = postingsList.get(docID);

		if (posting == null) {
			posting = createEmptyTermInDoc(docID);
			postingsList.addPosting(posting);
		}
		posting.addPosition(position);

		posting.increaseTF();
	}

	@Override
	public int count() {
		return this.termsMap.size();
	}

	@Override
	public PostingsList postingsList(String term) {
		if (termsMap.containsKey(term)) {
			return termsMap.get(term);
		} else {
			return this.createEmptyPostingsList();
		}
	}

	@Override
	public int hashCode() {
		return this.termsMap.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		AtomicTermIndex another = (AtomicTermIndex) obj;
		return MapUtils.mapsAreEqual(this.termsMap, another.termsMap);
	}

	@Override
	public void prepare(DocumentIndex docIndex) {
		int docCollectionSize = docIndex.count();
		TFIDFPostingComparator cmp = new TFIDFPostingComparator();

		Iterator<PostingsList> postingsListIterator = this.termsMap.values().iterator();

		while (postingsListIterator.hasNext()) {
			PostingsList postingsList = postingsListIterator.next();
			int documentFrequency = postingsList.size();

			Iterator<Posting> postingIterator = postingsList.iterator();

			while (postingIterator.hasNext()) {
				Posting posting = postingIterator.next();
				posting.calculateTFIDF(documentFrequency, docCollectionSize);
			}
			postingsList.sort(cmp);
		}
	}
}