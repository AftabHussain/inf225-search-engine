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
import edu.uci.ics.inf225.searchengine.index.IndexGlobals;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.Lexicon;
import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;
import edu.uci.ics.inf225.searchengine.index.TwoGram;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingsList;
import edu.uci.ics.inf225.searchengine.search.scoring.QueryScorer;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.CosineSimilarityQueryRanker;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.ScoringContributor;
import edu.uci.ics.inf225.searchengine.search.scoring.solvers.SlashesScoringContributor;
import edu.uci.ics.inf225.searchengine.search.scoring.updaters.SlashesScoringUpdater;
import edu.uci.ics.inf225.searchengine.search.scoring.updaters.TextCosineSimilarityScoreUpdater;
import edu.uci.ics.inf225.searchengine.search.scoring.updaters.TitleCosineSimilarityUpdater;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class BasicSearchEngine implements SearchEngine {

	private DocumentIndex docIndex;

	private MultiFieldTermIndex termIndex;

	private Lexicon lexicon;

	private TextTokenizer tokenizer;

	private ScoringContributor<Double> bodyCosineSimilarity;
	private ScoringContributor<Double> titleCosineSimilarity;
	private ScoringContributor<Byte> slashesContributor;

	public BasicSearchEngine() {
		tokenizer = createTokenizer();

		// Cosine similarity on body.
		bodyCosineSimilarity = new CosineSimilarityQueryRanker(new TextCosineSimilarityScoreUpdater());

		// Cosine similarity on title.
		titleCosineSimilarity = new CosineSimilarityQueryRanker(new TitleCosineSimilarityUpdater());

		// Number of slashes contributor.
		slashesContributor = new SlashesScoringContributor(new SlashesScoringUpdater());
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
			termIndex = (MultiFieldTermIndex) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
	}

	public QueryResult query(String query) throws QueryException {
		final int numberOfExpectedResults = 10;
		int numberOfResults = 0;

		if (query.trim().isEmpty()) {
			throw new QueryException("Query cannot be empty.");
		}

		long start = System.nanoTime();
		PageTokenStream stream = null;
		try {
			stream = tokenizer.tokenize(query);

			Map<Object, PostingsList> bodyPostings = new HashMap<>();

			Map<Object, PostingsList> titlePostings = new HashMap<>();

			List<String> singleQueryTerms = new LinkedList<>();
			List<TwoGram> twoGrams = new LinkedList<>();

			obtainQueryTerms(stream, singleQueryTerms, twoGrams);
			QueryScorer queryScorer = new QueryScorer();

			for (TwoGram twoGram : twoGrams) {
				Integer termID = lexicon.getTermID(twoGram);
				if (termID != null) {
					// Query only if the term has been seen before (present
					// in the lexicon).
					PostingsList bodyPostingsList = this.termIndex.getIndex(IndexGlobals.BODY_2GRAM_FIELD).postingsList(termID);
					if (bodyPostingsList != null) {
						bodyPostings.put(twoGram, bodyPostingsList);
					}

					PostingsList titlePostingsList = this.termIndex.getIndex(IndexGlobals.TITLE_2GRAM_FIELD).postingsList(termID);
					if (titlePostingsList != null) {
						titlePostings.put(twoGram, titlePostingsList);
					}
				}
			}

			bodyCosineSimilarity.score(twoGrams, bodyPostings, termIndex, docIndex, queryScorer, IndexGlobals.BODY_2GRAM_FIELD);
			titleCosineSimilarity.score(twoGrams, titlePostings, termIndex, docIndex, queryScorer, IndexGlobals.TITLE_2GRAM_FIELD);
			slashesContributor.score(null, bodyPostings, null, docIndex, queryScorer, null);
			slashesContributor.score(null, titlePostings, null, docIndex, queryScorer, null);

			numberOfResults = queryScorer.count();

			/*
			 * If we did not reach the expected number of results, will look for
			 * single terms.
			 */
			if (numberOfResults < numberOfExpectedResults) {
				bodyPostings.clear();
				titlePostings.clear();

				for (String queryTerm : singleQueryTerms) {
					Integer termID = lexicon.getTermID(queryTerm);
					if (termID != null) {
						// Query only if the term has been seen before
						// (present
						// in the lexicon).
						PostingsList bodyPostingsList = this.termIndex.getIndex(IndexGlobals.BODY_FIELD).postingsList(termID);
						if (bodyPostingsList != null) {
							bodyPostings.put(queryTerm, bodyPostingsList);
						}

						PostingsList titlePostingsList = this.termIndex.getIndex(IndexGlobals.TITLE_FIELD).postingsList(termID);
						if (titlePostingsList != null) {
							titlePostings.put(queryTerm, titlePostingsList);
						}
					}
				}

				bodyCosineSimilarity.score(singleQueryTerms, bodyPostings, termIndex, docIndex, queryScorer, IndexGlobals.BODY_FIELD);
				titleCosineSimilarity.score(singleQueryTerms, titlePostings, termIndex, docIndex, queryScorer, IndexGlobals.TITLE_FIELD);
				slashesContributor.score(null, bodyPostings, null, docIndex, queryScorer, null);
				slashesContributor.score(null, titlePostings, null, docIndex, queryScorer, null);
			}

			/*
			 * TODO REMOVE THIS FOR PRODUCTION!!!!!!!!!!
			 */
			// ScoringDebugger debugger = new ScoringDebugger();
			// debugger.dump(docIndex, queryScorer, termIndex, query +
			// ".query");

			QueryResult queryResult = createQueryResult(queryScorer.top(numberOfExpectedResults));
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

	private void obtainQueryTerms(PageTokenStream stream, List<String> allQueryTerms, List<TwoGram> twoGrams) {
		String previousTerm = null;
		while (stream.increment()) {
			PageToken token = stream.next();
			allQueryTerms.add(token.getTerm());

			if (previousTerm != null) {
				TwoGram twoGram = new TwoGram();
				twoGram.setTerms(previousTerm, token.getTerm());
				twoGrams.add(twoGram);
			}
			previousTerm = token.getTerm();
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