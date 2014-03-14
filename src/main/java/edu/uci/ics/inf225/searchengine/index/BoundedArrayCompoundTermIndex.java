package edu.uci.ics.inf225.searchengine.index;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;

public class BoundedArrayCompoundTermIndex extends CompoundTermIndex {

	private static final int DEFAULT_BACKETS = 16; // MUST BE POWER OF 2.

	private TermIndex[] indices;

	private int buckets;

	private static final long serialVersionUID = 1L;

	public BoundedArrayCompoundTermIndex() {
		this(DEFAULT_BACKETS);
	}

	/**
	 * @param buckets
	 *            IT MUST BE POWER OF 2.
	 */
	public BoundedArrayCompoundTermIndex(int buckets) {
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
	protected TermIndex getIndexFor(int term) {
		return indices[getBucket(term)];
	}

	private int getBucket(int term) {
		return term & (buckets - 1);
	}

	private AtomicTermIndex createAtomicIndex() {
		return new AtomicTermIndex(10000000 / buckets);
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

	@Override
	public void prepare(DocumentIndex docIndex) {
		Iterator<TermIndex> allIndices = this.getAllIndices();

		while (allIndices.hasNext()) {
			allIndices.next().prepare(docIndex);
		}
	}
}