package edu.uci.ics.inf225.searchengine.index.docs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.utils.MapUtils;

public class SimpleDocumentIndex implements DocumentIndex, Externalizable {

	private static final long serialVersionUID = 1L;

	private Map<Integer, String> docIDs = createDocIDsMap(100000);

	private Map<Integer, Set<Integer>> termIDsPerDoc = new HashMap<>();

	private Map<Integer, String> createDocIDsMap(int initialCapacity) {
		return new HashMap<>(initialCapacity);
	}

	public SimpleDocumentIndex() {
	}

	@Override
	public int addDoc(WebPage page) {
		Integer docID = DocIDGenerator.getInstance().next();
		synchronized (this) {
			this.docIDs.put(docID, page.getUrl());
		}
		return docID;
	}

	@Override
	public int count() {
		return this.docIDs.size();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(docIDs.size());

		for (Entry<Integer, String> entry : docIDs.entrySet()) {
			out.writeInt(entry.getKey());
			out.writeUTF(entry.getValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		this.docIDs = createDocIDsMap(size);

		for (int i = 0; i < size; i++) {
			docIDs.put(in.readInt(), in.readUTF());
		}
	}

	@Override
	public int hashCode() {
		return this.docIDs.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		SimpleDocumentIndex another = (SimpleDocumentIndex) obj;
		return MapUtils.mapsAreEqual(this.docIDs, another.docIDs);
	}

	@Override
	public WebPage getDoc(int docID) {
		WebPage page = new WebPage();
		page.setUrl(this.docIDs.get(docID));
		return page;
	}

	@Override
	public void shutdown() {
		// Nothing to do, so far.
	}

	@Override
	public void prepare(TermIndex termIndex) {
		// Do nothing, so far.
	}

	@Override
	public void addTerms(int docID, Set<Integer> termIDs) {
		this.termIDsPerDoc.put(docID, termIDs);
	}

	@Override
	public Set<Integer> getTerms(int docID) {
		return this.termIDsPerDoc.get(docID);
	}
}