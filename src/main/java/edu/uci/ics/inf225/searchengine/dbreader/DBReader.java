package edu.uci.ics.inf225.searchengine.dbreader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.similarity.Filter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenizer;

/**
 * @author Rezvan
 * 
 */
public class DBReader {

	private static final String DB_PATH = "db/crawlerdb";

	private static final int PAGES_TO_READ = 5; // -1 for unlimited.

	private Connection conn = null;

	private static final Logger log = LoggerFactory.getLogger(DBReader.class);

	private Filter filter;

	private PageTokenizer tokenizer;

	public DBReader() {
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public PageTokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(PageTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:" + DB_PATH, "sa", "");
	}

	public void shutdown() throws SQLException {
		Statement st = conn.createStatement();
		st.execute("SHUTDOWN");
		conn.close();
	}

	public void readDB() throws SQLException, ClassNotFoundException {
		connect();
		Statement st = null;
		ResultSet rs = null;
		st = conn.createStatement();
		rs = st.executeQuery("SELECT url,title,content FROM pages"); // run the
																		// select
																		// query

		// do something with the result set.
		long i = 0L;
		while (rs.next() && (PAGES_TO_READ > -1 && i < PAGES_TO_READ)) {
			String url = rs.getString(1);
			String title = rs.getString(2);
			try {
				String content = IOUtils.toString(rs.getClob(3).getCharacterStream());
				this.passPage(url, title, content);
				i++;
			} catch (IOException e) {
				log.error("Page {} could not be processed: {}", url, e.getMessage());
			}
		}
		st.close();
	}

	private boolean passPage(String url, String title, String content) throws IOException {
		if (filter.isUnique(url, content)) {
			tokenizer.tokenize(url, title, content);
			return true;
		} else {
			return false;
		}
	}
}