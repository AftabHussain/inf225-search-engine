package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class MultiFieldTermIndex implements Externalizable {

	private Map<String, TermIndex> indices;

	public MultiFieldTermIndex() {
		indices = new HashMap<>();
	}

	public TermIndex getIndex(String field) {
		return indices.get(field);
	}

	public void putIndex(String field, TermIndex index) {
		indices.put(field, index);
	}

	public void prepare(DocumentIndex docIndex) {
		for (TermIndex termIndex : indices.values()) {
			termIndex.prepare(docIndex);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(indices.size());
		for (Entry<String, TermIndex> eachIndex : indices.entrySet()) {
			out.writeUTF(eachIndex.getKey());
			out.writeObject(eachIndex.getValue());
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		this.indices = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			indices.put(in.readUTF(), (TermIndex) in.readObject());
		}
	}
}