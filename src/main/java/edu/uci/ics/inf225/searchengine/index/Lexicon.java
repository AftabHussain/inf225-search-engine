package edu.uci.ics.inf225.searchengine.index;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Lexicon implements Externalizable {

	// private Map<Object, Integer> terms;
	private TObjectIntHashMap<Object> terms;

	private int nextID;

	// private static final long serialVersionUID = 1L;
	private static final long serialVersionUID = 4471344787615638118L;

	public Lexicon() {
		this(10000000);
	}

	public Lexicon(int initialCapacity) {
		terms = new TObjectIntHashMap<>(initialCapacity);
		nextID = 1;
	}

	public int getTermID(Object term) {
		// Integer id = terms.get(term);
		//
		// if (id == null) {
		// id = nextID++;
		// terms.put(term, id);
		// }
		//
		// return id;
		if (!terms.contains(term)) {
			int id = nextID++;
			terms.put(term, id);
		}
		return terms.get(term);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(nextID);
		out.writeObject(terms);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		nextID = in.readInt();
		// terms = (Map<Object, Integer>) in.readObject();
		terms = (TObjectIntHashMap<Object>) in.readObject();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		builder.append(terms);
		builder.append(nextID);

		return builder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Lexicon lexicon2 = (Lexicon) obj;
		// return this.nextID == lexicon2.nextID &&
		// MapUtils.mapsAreEqual(this.terms, lexicon2.terms);
		return this.nextID == lexicon2.nextID && this.terms.equals(lexicon2.terms);
	}

	public int size() {
		return this.terms.size();
	}
}
