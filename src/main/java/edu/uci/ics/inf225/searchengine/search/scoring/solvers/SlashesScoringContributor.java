package edu.uci.ics.inf225.searchengine.search.scoring.solvers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.DocScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;

public class SlashesScoringContributor extends ScoringContributor<Byte> {

	public SlashesScoringContributor(DocScorerUpdater<Byte> scoreUpdater) {
		super(scoreUpdater);
	}

	@Override
	public void score(List<? extends Object> allQueryTerms, Map<Object, PostingsList> postingsLists, MultiFieldTermIndex termIndex, DocumentIndex docIndex, QueryScorer queryScorer, String field) {
		Set<Integer> docIDs = extractDocIDs(postingsLists);

		for (Integer docID : docIDs) {
			DocScorer docScorer = queryScorer.getScorer(docID);

			WebPage doc = docIndex.getDoc(docID);

			this.getScoreUpdater().update(docScorer, doc.getSlashes());
		}
	}

	private Set<Integer> extractDocIDs(Map<Object, PostingsList> postingsLists) {
		Set<Integer> docIDs = new HashSet<>();
		for (PostingsList postingsList : postingsLists.values()) {
			Iterator<Posting> iterator = postingsList.iterator();

			while (iterator.hasNext()) {
				Posting posting = iterator.next();

				docIDs.add(posting.getDocID());
			}
		}

		return docIDs;
	}

}
