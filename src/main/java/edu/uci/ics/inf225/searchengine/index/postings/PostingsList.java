package edu.uci.ics.inf225.searchengine.index.postings;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.ListUtils;

public class PostingsList implements Externalizable {

	private static final long serialVersionUID = 1L;

	private List<Posting> postings;

	public PostingsList() {
		// TODO Look for better data structure. Skip-list?
		// postings = new FastSortedMap<Integer, Posting>();
		postings = new LinkedList<>();
	}

	public List<Posting> postings() {
		return Collections.unmodifiableList(this.postings);
	}

	public void sort(Comparator<Posting> comparator) {
		Collections.sort(postings, comparator);
	}

	public void addPosting(Posting posting) {
		// TODO Use strategies.
		this.addSortedByDocIDDesc(posting);
	}

	private void addSortedByDocIDDesc(Posting posting) {
		if (postings.isEmpty()) {
			postings.add(posting);
		} else {

			ListIterator<Posting> listIterator = postings.listIterator();
			while (listIterator.hasNext()) {
				Posting next = listIterator.next();
				if (posting.getDocID() > next.getDocID()) {
					listIterator.previous();
					listIterator.add(posting);
					return;
				} else if (posting.getDocID() == next.getDocID()) {
					next.merge(posting);
				}
			}
		}
	}

	public Iterator<Posting> iterator() {
		return postings.iterator();
	}

	public Posting get(int docID) {
		return getWhenItIsSortedByDocID(docID);
	}

	private Posting getWhenItIsSortedByDocID(int docID) {
		ListIterator<Posting> listIterator = postings.listIterator();
		while (listIterator.hasNext()) {
			Posting next = listIterator.next();
			if (docID == next.getDocID()) {
				return next;
			} else if (docID > next.getDocID()) {
				// Don't keep searching.
				return null;
			}
		}
		return null;
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
	public void writeExternal(ObjectOutput out) throws IOException {
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
