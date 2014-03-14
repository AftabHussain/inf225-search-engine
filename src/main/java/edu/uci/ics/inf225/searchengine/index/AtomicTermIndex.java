package edu.uci.ics.inf225.searchengine.index;

import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.DocIDPostingComparator;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtomicTermIndex implements TermIndex, Externalizable {

	private static final Logger log = LoggerFactory.getLogger(AtomicTermIndex.class);

	private static final long serialVersionUID = 1L;

	// private Map<Integer, PostingsList> termsMap;
	private TIntObjectHashMap<PostingsList> termsMap;

	private Lexicon lexicon;

	// private HashMap<Integer, PostingsList> createTermsMap(int
	// initialCapacity) {
	// return new HashMap<>(initialCapacity);
	private TIntObjectHashMap<PostingsList> createTermsMap(int initialCapacity) {
		return new TIntObjectHashMap<>(initialCapacity);
	}

	public AtomicTermIndex() {
		this(3500000);
	}

	public Lexicon getLexicon() {
		return lexicon;
	}

	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}

	public AtomicTermIndex(int initialCapacity) {
		termsMap = createTermsMap(initialCapacity);
	}

	private Posting createEmptyPosting(int docID) {
		return new Posting(docID, 0);
	}

	private PostingsList createEmptyPostingsList() {
		return new PostingsList();
	}

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(termsMap.size());
		// for (Entry<Integer, PostingsList> entry : termsMap.entrySet()) {
		// out.writeInt(entry.getKey());
		// out.writeObject(entry.getValue());
		// }
		termsMap.forEachEntry(new TIntObjectProcedure<PostingsList>() {

			@Override
			public boolean execute(int a, PostingsList b) {
				try {
					out.writeInt(a);
					out.writeObject(b);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int size = in.readInt();
		termsMap = createTermsMap(size);

		for (int i = 0; i < size; i++) {
			termsMap.put(in.readInt(), (PostingsList) in.readObject());
		}
	}

	@Override
	public void newTerm(int docID, int termID) {
		PostingsList postingsList;
		Posting posting;

		postingsList = termsMap.get(termID);

		if (postingsList == null) {
			postingsList = createEmptyPostingsList();
			termsMap.put(termID, postingsList);
		}

		posting = postingsList.get(docID);

		if (posting == null) {
			posting = createEmptyPosting(docID);
			postingsList.addPosting(posting);
		}
		posting.increaseTF();
	}

	@Override
	public int count() {
		return this.termsMap.size();
	}

	@Override
	public int hashCode() {
		return this.termsMap.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		AtomicTermIndex another = (AtomicTermIndex) obj;
		// return MapUtils.mapsAreEqual(this.termsMap, another.termsMap);
		return this.termsMap.equals(another.termsMap);
	}

	@Override
	public void prepare(DocumentIndex docIndex) {
		int docCollectionSize = docIndex.count();

		// Iterator<PostingsList> postingsListIterator =
		// this.termsMap.values().iterator();
		Iterator<PostingsList> postingsListIterator = this.termsMap.valueCollection().iterator();

		while (postingsListIterator.hasNext()) {
			PostingsList postingsList = postingsListIterator.next();
			int documentFrequency = postingsList.size();

			Iterator<Posting> postingIterator = postingsList.iterator();

			while (postingIterator.hasNext()) {
				Posting posting = postingIterator.next();
				posting.calculateTFIDF(documentFrequency, docCollectionSize);
			}
			/*
			 * Sort it by DocID to compute cosine similarity faster.
			 */
			postingsList.sort(new DocIDPostingComparator());
		}
	}

	@Override
	public double idf(int termID) {
		PostingsList postingsList = this.postingsList(termID);

		if (postingsList == null) {
			return 0d;
		}

		return (double) this.termsMap.size() / (double) postingsList.size();
	}

	@Override
	public PostingsList postingsList(int termID) {
		PostingsList postingsList = termsMap.get(termID);

		if (postingsList == null) {
			return this.createEmptyPostingsList();
		} else {
			return postingsList;
		}
	}
}