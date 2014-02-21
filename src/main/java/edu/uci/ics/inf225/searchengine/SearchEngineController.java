package edu.uci.ics.inf225.searchengine;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.DBReader;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.similarity.DuplicateFilter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenizer;

public class SearchEngineController {

	private Indexer indexer;
	private DBReader reader;

	private static final Logger console = LoggerFactory.getLogger("console");

	public SearchEngineController() {
	}

	public void start() throws SQLException, ClassNotFoundException {
		indexer = new Indexer();
		PageTokenizer tokenizer = new PageTokenizer();

		tokenizer.setIndexer(indexer);

		reader = new DBReader();

		reader.setFilter(new DuplicateFilter());
		reader.setTokenizer(tokenizer);

		reader.readDB();
	}

	public void shutdown() throws IOException {
		try {
			this.reader.shutdown();
		} catch (SQLException e) {
			console.error("An error occurred when shutting down DB Reader: {}", e.getMessage());
		}
		this.indexer.save();
	}
}
