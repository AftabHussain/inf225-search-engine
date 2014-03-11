package edu.uci.ics.inf225.searchengine.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.uci.ics.inf225.searchengine.utils.MapUtils;

public class Lexicon implements Externalizable {

	private Map<String, Integer> terms;

	private int nextID;

	public Lexicon() {
		terms = new HashMap<>(3000000);
		nextID = 1;
	}

	public Integer getTermID(String term) {
		Integer id = terms.get(term);

		if (id == null) {
			id = nextID++;
			terms.put(term, id);
		}

		return id;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(nextID);
		out.writeObject(terms);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		nextID = in.readInt();
		terms = (Map<String, Integer>) in.readObject();
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
		return this.nextID == lexicon2.nextID && MapUtils.mapsAreEqual(this.terms, lexicon2.terms);
	}
}
