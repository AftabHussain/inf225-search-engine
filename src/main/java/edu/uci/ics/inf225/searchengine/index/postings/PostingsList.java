package edu.uci.ics.inf225.searchengine.index.postings;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.inf225.searchengine.utils.MapUtils;

public class PostingsList implements Externalizable {

	private Map<Integer, Posting> postings;

	public PostingsList() {
		// TODO Look for better data structure. Skip-list?
		// postings = new FastSortedMap<Integer, Posting>();
		postings = new HashMap<>();
	}

	public void merge(PostingsList anotherList) {
		for (Entry<Integer, Posting> anotherPosting : anotherList.postings.entrySet()) {

			Posting posting = postings.get(anotherPosting.getKey());

			if (posting != null) {
				// This list contains the posting, merge them.
				posting.merge(anotherPosting.getValue());
			} else {
				postings.put(anotherPosting.getKey(), anotherPosting.getValue());
			}
		}
	}

	public void addPosting(Posting posting) {
		postings.put(posting.getDocID(), posting);
	}

	public Iterator<Posting> iterator() {
		return postings.values().iterator();
	}

	public Posting get(int docID) {
		return postings.get(docID);
	}

	public int size() {
		return postings.size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		for (Entry<Integer, Posting> entry : postings.entrySet()) {
			builder.append("(doc#").append(entry.getKey()).append(":").append(entry.getValue()).append("), ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append("]");

		return builder.toString();
	}

	@Override
	public int hashCode() {
		return postings.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		PostingsList another = (PostingsList) obj;
		return MapUtils.mapsAreEqual(this.postings, another.postings);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(postings);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		postings = (Map<Integer, Posting>) in.readObject();
	}
}
