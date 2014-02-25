package edu.uci.ics.inf225.searchengine.dbreader;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.utils.HTMLUtils;

/**
 * @author Matias Giorgio.
 * 
 */
public class DBReader {

	private static final String DB_PATH = "db/crawlerdb";

	private static final int PAGES_TO_READ = -1; // -1 for unlimited.
	// private static final int PAGES_TO_READ = 10000; // -1 for unlimited.

	private Connection conn = null;

	private static final Logger log = LoggerFactory.getLogger(DBReader.class);

	private static final Logger console = LoggerFactory.getLogger("console");

	private static final long MAX_CLOB_SIZE = 5 * 1024 * 1024; // 2 MB.

	private Statement st = null;

	private ResultSet rs = null;

	public DBReader() {
	}

	public void start() throws ClassNotFoundException, SQLException {
		connect();
		init();
	}

	public int count() throws SQLException {
		ResultSet rs = st.executeQuery("select count(1) from PAGES");

		int count = 0;
		if (rs.next()) {
			count = rs.getInt(1);
		}

		rs.close();

		return count;
	}

	private void init() throws SQLException {
		st = conn.createStatement();
		String query = null;

		if (PAGES_TO_READ == -1) {
			query = "SELECT url,title,content FROM pages";
		} else {
			query = "SELECT LIMIT 0 " + PAGES_TO_READ + " url,title,content FROM pages";
		}

		rs = st.executeQuery(query);
	}

	private void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.hsqldb.jdbcDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:file:" + DB_PATH, "sa", "");
	}

	public void shutdown() throws SQLException {
		quietlyCloseResultSet();
		st.execute("SHUTDOWN");
		quietlyCloseStatement();
		conn.close();
	}

	private void quietlyCloseStatement() {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				log.warn("Statement could not be closed", e);
			}
		}
	}

	private void quietlyCloseResultSet() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.warn("Result Set could not be closed", e);
			}
		}
	}

	public Iterator<WebPage> iterator() {
		WebPageIterator iterator = new WebPageIterator();

		return iterator;
	}

	private class WebPageIterator implements Iterator<WebPage> {

		private WebPage page;

		public WebPageIterator() {
		}

		@Override
		public boolean hasNext() {
			try {
				while (rs.next() && page == null) {
					Clob clob = rs.getClob(3);
					String url = rs.getString(1);
					if (clob.length() < MAX_CLOB_SIZE) {
						String content = IOUtils.toString(clob.getCharacterStream());
						page = new WebPage();
						page.setUrl(url);
						page.setTitle(rs.getString(2));
						page.setHtmlContent(content);
						page.setContent(HTMLUtils.parse(content));
					} else {
						log.info("Skipping {} with size {}", url, clob.length());
					}
				}
				return page != null;
			} catch (SQLException e) {
				log.error("Error accessing DB", e);
				return false;
			} catch (IOException e) {
				log.error("Error accessing DB", e);
				return false;
			}
		}

		@Override
		public WebPage next() {
			WebPage pageToReturn = page;
			page = null;
			return pageToReturn;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}