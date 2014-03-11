package edu.uci.ics.inf225.searchengine.search.scoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryScorer {

	private Map<Integer, DocScorer> docScorer;

	public QueryScorer() {
		this.docScorer = new HashMap<>();
	}

	/**
	 * This method will retrieve the {@link DocScorer} for a given document. If
	 * the {@link DocScorer} does not exist, it will be created.
	 * 
	 * @param docID
	 *            The ID of the Document get the {@link DocScorer} of.
	 * @return A non-<code>null</code> {@link DocScorer} for the given document
	 *         ID.
	 */
	public DocScorer getScorer(int docID) {
		DocScorer scorer = this.docScorer.get(docID);

		if (scorer == null) {
			scorer = createEmptyScorer(docID);
			this.docScorer.put(docID, scorer);
		}

		return scorer;
	}

	private DocScorer createEmptyScorer(int docID) {
		DocScorer docScorer = new DocScorer();
		docScorer.setDocID(docID);
		return docScorer;
	}

	public int count() {
		return this.docScorer.size();
	}

	public List<Integer> top(int n) {
		// It must have random access for performance reasons.
		ArrayList<DocScorer> docScorers = new ArrayList<>(this.docScorer.values());

		Collections.sort(docScorers);

		// TODO Improve algorithm.
		Collections.reverse(docScorers);

		List<Integer> topDocs = new ArrayList<>(n);
		if (docScorers.size() < n) {
			for (DocScorer docScorer : docScorers) {
				topDocs.add(docScorer.getDocID());
			}
		} else {
			// for (int i = docScorers.size() - n; i < docScorers.size(); i++) {
			for (int i = 0; i < n; i++) {
				topDocs.add(docScorers.get(i).getDocID());
			}
		}

		return topDocs;
	}
}
