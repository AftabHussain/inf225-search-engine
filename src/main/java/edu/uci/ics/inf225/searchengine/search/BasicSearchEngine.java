package edu.uci.ics.inf225.searchengine.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.Posting;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class BasicSearchEngine implements SearchEngine {

	private DocumentIndex docIndex;

	private TermIndex termIndex;

	private TextTokenizer tokenizer;

	public BasicSearchEngine() {
		tokenizer = createTokenizer();
	}

	public void start() throws IOException {
		readIndexFromDisk();
		tokenizer.start();
	}

	private TextTokenizer createTokenizer() {
		return new TextTokenizer();
	}

	public void shutdown() {
		tokenizer.stop();
	}

	private void readIndexFromDisk() throws IOException {
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Indexer.INDEX_FILENAME));

		try {
			docIndex = (DocumentIndex) inputStream.readObject();
			termIndex = (TermIndex) inputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			inputStream.close();
		}
	}

	private List<Integer> rank(List<Posting> postings) {
		// TODO Aftab to completely rewrite this method.
		List<Integer> docIDs = new ArrayList<>(postings.size());

		for (Posting posting : postings) {
			docIDs.add(posting.getDocID());
		}

		return docIDs;
	}

	public QueryResult query(String query) throws QueryException {
		long start = System.nanoTime();
		PageTokenStream stream = null;
		try {
			stream = tokenizer.tokenize(query);

			List<Posting> postings = new LinkedList<>();
			while (stream.increment()) {
				PageToken token = stream.next();
				System.out.println("#" + token.getTerm());
				postings.addAll(this.termIndex.postingsList(token.getTerm()).postings());
			}

			QueryResult queryResult = createQueryResult(rank(postings));
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
		QueryResult queryResult = new QueryResult();

		for (Integer docID : rank) {
			queryResult.addEntry(createQueryResultEntry(docID));
		}

		return queryResult;
	}

	private QueryResultEntry createQueryResultEntry(Integer docID) {
		QueryResultEntry entry = new QueryResultEntry();
		entry.setUrl(docIndex.getDoc(docID));

		return entry;
	}
}