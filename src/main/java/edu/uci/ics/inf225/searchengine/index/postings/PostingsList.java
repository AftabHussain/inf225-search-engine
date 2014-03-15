package edu.uci.ics.inf225.searchengine.index.postings;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.collections.ListUtils;

public class PostingsList implements Externalizable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Posting> postings;

	public PostingsList() {
		postings = new ArrayList<>();
	}

	public Collection<Posting> postings() {
		return Collections.unmodifiableList(this.postings);
	}

	public void addPosting(Posting posting) {
		postings.add(posting);
	}

	public Iterator<Posting> iterator() {
		return postings.iterator();
	}

	public Posting get(int docID) {
		int index = binarySearch0(docID);
		if (index > -1) {
			return postings.get(index);
		} else {
			return null;
		}
	}

	private int binarySearch0(int docID) {
		int low = 0;
		int high = postings.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = postings.get(mid).getDocID();
			int cmp = midVal - docID;
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}

	public int size() {
		return postings.size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		for (Posting entry : postings) {
			builder.append(entry).append("), ");
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
		return ListUtils.isEqualList(postings, another.postings);
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(postings.size());
		for (Posting posting : postings) {
			out.writeObject(posting);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int count = in.readInt();
		postings = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			postings.add((Posting) in.readObject());
		}
	}
}
