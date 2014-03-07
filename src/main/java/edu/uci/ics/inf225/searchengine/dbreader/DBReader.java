package edu.uci.ics.inf225.searchengine.dbreader;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.jsoup.nodes.Document;
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
	// private static final int PAGES_TO_READ = 2000;

	private Connection conn = null;

	private static final Logger log = LoggerFactory.getLogger(DBReader.class);

	private static final Logger console = LoggerFactory.getLogger("console");

	private Statement st = null;

	private ResultSet rs = null;

	public DBReader() {
	}

	public void start() throws ClassNotFoundException, SQLException {
		connect();
		init();
	}

	public int count() throws SQLException {
		ResultSet rs = st.executeQuery("select count(*) from PAGES");

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
		return this.iterator(PredicateUtils.truePredicate());
	}

	public Iterator<WebPage> iterator(Predicate predicate) {
		WebPageIterator iterator = new WebPageIterator(predicate);

		return iterator;
	}

	private class WebPageIterator implements Iterator<WebPage> {

		private WebPage page;

		private Predicate predicate;

		public WebPageIterator(Predicate predicate) {
			this.predicate = predicate;
		}

		@Override
		public boolean hasNext() {
			try {
				while (page == null && rs.next()) {
					String url = rs.getString(1);
					Clob clob = rs.getClob(3);
					if (predicate.evaluate(rs)) {
						String content = IOUtils.toString(clob.getCharacterStream());
						page = new WebPage();
						page.setUrl(url);
						String title = (String) ObjectUtils.defaultIfNull(rs.getString(2), "");
						page.setTitle(title);
						page.setHtmlContent(content);
						extractHTMLInformation(page, content);
					} else {
						log.info("Skipping {} with size {}", url, clob.length());
					}
				}
				return page != null;
			} catch (SQLException e) {
				log.error("Error accessing DB", e);
				System.out.println("DBReader.WebPageIterator.hasNext()");
				return false;
			} catch (IOException e) {
				System.out.println("DBReader.WebPageIterator.hasNext()");
				log.error("Error accessing DB", e);
				return false;
			}
		}

		private void extractHTMLInformation(WebPage page, String content) {
			Document doc = HTMLUtils.parse(content);
			page.setContent(HTMLUtils.extractBody(doc));

			// TODO Aftab to extract links with anchors and put them in page by
			// doing page.addLink().
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