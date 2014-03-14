package edu.uci.ics.inf225.searchengine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.ClobSizePredicate;
import edu.uci.ics.inf225.searchengine.dbreader.DBReader;
import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.IndexGlobals;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.Lexicon;
import edu.uci.ics.inf225.searchengine.index.MultiFieldTermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.docs.HSQLDocumentIndex;
import edu.uci.ics.inf225.searchengine.index.postings.PostingGlobals;
import edu.uci.ics.inf225.searchengine.similarity.IdenticalFilter;
import edu.uci.ics.inf225.searchengine.similarity.SimilarityFilter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class IndexWriterController {

	private Indexer indexer;

	private DBReader reader;

	private DocumentIndex docIndex;

	private MultiFieldTermIndex termIndex;

	private TextTokenizer tokenizer;

	private SimilarityFilter filter;

	private Lexicon lexicon;

	private static final Logger dupsLogger = LoggerFactory.getLogger("duplogger");

	private static final Logger console = LoggerFactory.getLogger("console");

	private static final Logger log = LoggerFactory.getLogger(IndexWriterController.class);

	private static final long MAX_CLOB_SIZE = 1 * 1024 * 1024; // 1 MB.

	/*
	 * PageTokenizer optimizations (re-use objects)
	 */
	private PageToken cachedPageToken = new PageToken();

	public IndexWriterController() {
		lexicon = createLexicon();
		docIndex = createDocumentIndex();
		termIndex = createTermIndex();
		indexer = createIndexer();
		tokenizer = createPageTokenizer();
		reader = createDBReader();
		filter = createSimilarityFilter();
	}

	private Lexicon createLexicon() {
		return new Lexicon();
	}

	private DBReader createDBReader() {
		return new DBReader();
	}

	private TextTokenizer createPageTokenizer() {
		return new TextTokenizer();
	}

	private Indexer createIndexer() {
		return new Indexer(termIndex, docIndex, lexicon);
	}

	private MultiFieldTermIndex createTermIndex() {
		return new MultiFieldTermIndex();
	}

	private DocumentIndex createDocumentIndex() {
		// return new SimpleDocumentIndex();
		try {
			HSQLDocumentIndex documentIndex = new HSQLDocumentIndex();
			documentIndex.destroyDatabase();
			return documentIndex;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private IdenticalFilter createSimilarityFilter() {
		return new IdenticalFilter();
	}

	public void start() throws SQLException, ClassNotFoundException {
		tokenizer.start();
		reader.start();

		console.info("About to process {} pages from DB", reader.count());

		Iterator<WebPage> iterator = reader.iterator(new ClobSizePredicate(MAX_CLOB_SIZE, 3));

		WebPage page = null;
		int counter = 0;
		while (iterator.hasNext()) {
			page = iterator.next();

			if (isPageUnique(page)) {
				processPage(page);
			} else {
				dupsLogger.info("Duplicate: [{}]", page.getUrl());
			}

			counter++;
			if ((counter % 1000) == 0) {
				indexer.printIndexStats();
			}
		}
	}

	private boolean isPageUnique(WebPage page) {
		return !filter.isDuplicate(page.getUrl(), page.getContent());
	}

	private void processPage(WebPage page) {
		int docID = docIndex.addDoc(page);

		try {
			/*
			 * Tokenize page contents (body).
			 */
			PageTokenStream tokenStream = tokenizer.tokenize(page.getContent(), cachedPageToken);
			processTokenStream(docID, tokenStream, IndexGlobals.BODY_FIELD);

			/*
			 * Tokenize page title.
			 */
			tokenStream = tokenizer.tokenize(page.getTitle(), cachedPageToken);
			processTokenStream(docID, tokenStream, IndexGlobals.TITLE_FIELD);

			/*
			 * TODO Process anchor links.
			 */
		} catch (IOException e) {
			log.error("Page {} could not be processed: {}", page.getUrl(), e.getMessage());
		}
	}

	/**
	 * Processes a {@link PageTokenStream} for a document.
	 * 
	 * @param docID
	 *            The ID of the document related to this token stream.
	 * @param tokenStream
	 *            The {@link PageTokenStream}.
	 * @param type
	 *            One value from {@link PostingGlobals}.
	 * @throws IOException
	 */
	private void processTokenStream(int docID, PageTokenStream tokenStream, String field) throws IOException {
		List<String> terms = new LinkedList<>();
		while (tokenStream.increment()) {
			PageToken token = tokenStream.next();
			terms.add(token.getTerm());
		}
		tokenStream.close();
		if (!terms.isEmpty()) {
			indexer.indexTerms(terms, docID, field);
		}
	}

	public void prepareIndex() {
		console.info("Preparing index...");
		this.termIndex.prepare(docIndex);
		this.docIndex.prepare(termIndex.getIndex(IndexGlobals.BODY_FIELD));
		console.info("Index has been prepared.");
	}

	public void shutdown() throws IOException {
		try {
			this.reader.shutdown();
		} catch (SQLException e) {
			console.error("An error occurred when shutting down DB Reader: {}", e.getMessage());
		}
		this.tokenizer.stop();
		this.indexer.save();
	}
}
