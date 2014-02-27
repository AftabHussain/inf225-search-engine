package edu.uci.ics.inf225.searchengine.index;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

public class LexicalCompoundTermIndex extends CompoundTermIndex {

	private static final int DEFAULT_BACKETS = 10;

	private TermIndex[] indices;

	private int buckets;

	// private static final long serialVersionUID = 1L;

	private static final long serialVersionUID = -8679163397290854600L;

	public LexicalCompoundTermIndex() {
		this(DEFAULT_BACKETS);
	}

	public LexicalCompoundTermIndex(int buckets) {
		this.buckets = buckets;
		initIndices();
	}

	private void initIndices() {
		indices = new TermIndex[buckets];
		for (int i = 0; i < buckets; i++) {
			indices[i] = createAtomicIndex();
		}
	}

	@Override
	protected TermIndex getIndexFor(String term) {
		return indices[getBucket(term)];
	}

	private int getBucket(String term) {
		return hash(term) & (buckets - 1);
	}

	private int hash(CharSequence csq) {
		if (csq == null)
			return 0;
		int n = csq.length();
		if (n == 0)
			return 0;
		// Hash based on 5 characters only.
		return csq.charAt(0) + csq.charAt(n - 1) * 31 + csq.charAt(n >> 1) * 1009 + csq.charAt(n >> 2) * 27583 + csq.charAt(n - 1 - (n >> 2)) * 73408859;
	}

	private AtomicTermIndex createAtomicIndex() {
		return new AtomicTermIndex();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterator<TermIndex> getAllIndices() {
		return IteratorUtils.arrayIterator(indices);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(buckets);
		for (int i = 0; i < buckets; i++) {
			out.writeObject(indices[i]);
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		buckets = in.readInt();
		indices = new TermIndex[buckets];

		for (int i = 0; i < buckets; i++) {
			indices[i] = (TermIndex) in.readObject();
		}
	}

}
