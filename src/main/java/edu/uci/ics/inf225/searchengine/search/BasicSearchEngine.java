package edu.uci.ics.inf225.searchengine.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.Lexicon;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.CosineSimilarityQueryRanker;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.ScoringContributor;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class BasicSearchEngine implements SearchEngine {

	private DocumentIndex docIndex;

	private TermIndex termIndex;

	private Lexicon lexicon;

	private TextTokenizer tokenizer;

	private ScoringContributor cosineSimRanker;

	public BasicSearchEngine() {
		tokenizer = createTokenizer();
		cosineSimRanker = new CosineSimilarityQueryRanker();
	}

	public void start(String indexFilename) throws IOException {
		readIndexFromDisk(indexFilename);
		tokenizer.start();
	}

	public void start() throws IOException {
		start(Indexer.INDEX_FILENAME);
	}

	private TextTokenizer createTokenizer() {
		return new TextTokenizer();
	}

	public void shutdown() {
		tokenizer.stop();
		docIndex.shutdown();
	}

	private void readIndexFromDisk(String filename) throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));

		try {
			lexicon = (Lexicon) inputStream.readObject();
			docIndex = (DocumentIndex) inputStream.readObject();
			termIndex = (TermIndex) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
	}

	public QueryResult query(String query) throws QueryException {
		if (query.trim().isEmpty()) {
			throw new QueryException("Query cannot be empty.");
		}
		long start = System.nanoTime();
		PageTokenStream stream = null;
		try {
			stream = tokenizer.tokenize(query);

			Map<String, PostingsList> postings = new HashMap<>();

			List<String> allQueryTerms = new LinkedList<>();

			while (stream.increment()) {
				PageToken token = stream.next();
				allQueryTerms.add(token.getTerm());
			}

			for (String queryTerm : allQueryTerms) {
				if (!postings.containsKey(queryTerm)) {
					/*
					 * Process only if this query term has not been processed
					 * before.
					 */
					Integer termID = lexicon.getTermID(queryTerm);
					if (termID != null) {
						postings.put(queryTerm, this.termIndex.postingsList(termID));
					}
				}
			}

			QueryScorer queryScorer = new QueryScorer();

			this.cosineSimRanker.score(allQueryTerms, postings, termIndex, docIndex, queryScorer);

			QueryResult queryResult = createQueryResult(queryScorer.top(10));
			queryResult.setTotalPages(queryScorer.count());
			queryResult.setExecutionTime(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
			return queryResult;
		} catch (IOException e) {
			throw new QueryException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new QueryException(e);
			}
		}
	}

	private QueryResult createQueryResult(List<Integer> rank) {
		QueryResult queryResult = new QueryResult(rank.size());

		for (Integer docID : rank) {
			queryResult.addEntry(createQueryResultEntry(docID));
		}

		return queryResult;
	}

	private QueryResultEntry createQueryResultEntry(Integer docID) {
		QueryResultEntry entry = new QueryResultEntry();
		WebPage page = docIndex.getDoc(docID);
		entry.setUrl(page.getUrl());
		entry.setTitle(page.getTitle());
		entry.setContent(page.getContent());

		return entry;
	}
}