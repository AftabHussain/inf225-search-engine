package edu.uci.ics.inf225.searchengine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.ClobSizePredicate;
import edu.uci.ics.inf225.searchengine.dbreader.DBReader;
import edu.uci.ics.inf225.searchengine.dbreader.WebPage;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.index.StringHashCompoundTermIndex;
import edu.uci.ics.inf225.searchengine.index.TermIndex;
import edu.uci.ics.inf225.searchengine.index.docs.DocumentIndex;
import edu.uci.ics.inf225.searchengine.index.docs.SimpleDocumentIndex;
import edu.uci.ics.inf225.searchengine.similarity.IdenticalFilter;
import edu.uci.ics.inf225.searchengine.similarity.SimilarityFilter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageToken;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenStream;
import edu.uci.ics.inf225.searchengine.tokenizer.TextTokenizer;

public class IndexWriterController {

	private Indexer indexer;

	private DBReader reader;

	private DocumentIndex docIndex;

	private TermIndex termIndex;

	private TextTokenizer tokenizer;

	private SimilarityFilter filter;

	private static final Logger dupsLogger = LoggerFactory.getLogger("duplogger");

	private static final Logger console = LoggerFactory.getLogger("console");

	private static final Logger log = LoggerFactory.getLogger(IndexWriterController.class);

	private static final long MAX_CLOB_SIZE = 5 * 1024 * 1024; // 5 MB.

	/*
	 * PageTokenizer optimizations (re-use objects)
	 */
	private PageToken cachedPageToken = new PageToken();

	public IndexWriterController() {
		docIndex = createDocumentIndex();
		termIndex = createTermIndex();
		indexer = createIndexer();
		tokenizer = createPageTokenizer();
		reader = createDBReader();
		filter = createSimilarityFilter();
	}

	private DBReader createDBReader() {
		return new DBReader();
	}

	private TextTokenizer createPageTokenizer() {
		return new TextTokenizer();
	}

	private Indexer createIndexer() {
		return new Indexer(termIndex, docIndex);
	}

	private TermIndex createTermIndex() {
		// return new AtomicTermIndex();
		return new StringHashCompoundTermIndex(100);
	}

	private SimpleDocumentIndex createDocumentIndex() {
		return new SimpleDocumentIndex();
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
		int docID = docIndex.addDoc(page.getUrl());

		try {
			PageTokenStream tokenStream = tokenizer.tokenize(page, cachedPageToken);

			processTokenStream(docID, tokenStream);
		} catch (IOException e) {
			log.error("Page {} could not be processed: {}", page.getUrl(), e.getMessage());
		}
	}

	private void processTokenStream(int docID, PageTokenStream tokenStream) throws IOException {
		while (tokenStream.increment()) {
			PageToken token = tokenStream.next();

			indexer.indexTerm(token.getTerm(), docID, token.getPosition());
		}
		tokenStream.close();
	}

	public void prepareIndex() {
		console.info("Preparing index...");
		this.termIndex.prepare(docIndex);
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
