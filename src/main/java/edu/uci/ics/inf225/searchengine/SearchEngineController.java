package edu.uci.ics.inf225.searchengine;

import java.io.IOException;
import java.sql.SQLException;

import edu.uci.ics.inf225.searchengine.dbreader.DBReader;
import edu.uci.ics.inf225.searchengine.index.Indexer;
import edu.uci.ics.inf225.searchengine.similarity.Filter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenizer;

public class SearchEngineController {

	private Indexer indexer;

	public SearchEngineController() {
	}

	public void start() throws SQLException {
		indexer = new Indexer();
		PageTokenizer tokenizer = new PageTokenizer();

		tokenizer.setIndexer(indexer);

		DBReader reader = new DBReader();

		reader.setFilter(new Filter());
		reader.setTokenizer(tokenizer);

		reader.readDB();
	}

	public void shutdown() throws IOException {
		this.indexer.save();
	}
}
