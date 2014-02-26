package edu.uci.ics.inf225.searchengine.index.docs;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.inf225.searchengine.utils.MapUtils;

public class SimpleDocumentIndex implements DocumentIndex, Externalizable {
	
	private static final long serialVersionUID = 1L;

	private Map<Integer, String> docIDs = createDocIDsMap(100000);

	private Map<Integer, String> createDocIDsMap(int initialCapacity) {
		return new HashMap<>(initialCapacity);
	}

	public SimpleDocumentIndex() {
	}

	@Override
	public int addDoc(String name) {
		Integer docID = DocIDGenerator.getInstance().next();
		this.docIDs.put(docID, name);
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
	public String getDoc(int docID) {
		return this.docIDs.get(docID);
	}
}