package edu.uci.ics.inf225.searchengine.dbreader;

import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.similarity.SimilarityFilter;
import edu.uci.ics.inf225.searchengine.tokenizer.PageTokenizer;

/**
 * @author Matias Giorgio.
 * 
 */
public class DBReader {

	private static final String DB_PATH = "db/crawlerdb";

	private static final int PAGES_TO_READ = -1; // -1 for unlimited.

	private Connection conn = null;

	private static final Logger log = LoggerFactory.getLogger(DBReader.class);

	private SimilarityFilter filter;

	private PageTokenizer tokenizer;

	private static final Logger console = LoggerFactory.getLogger("console");

	private static final Logger dupsLogger = LoggerFactory.getLogger("duplogger");

	private static final long MAX_CLOB_SIZE = 2 * 1024 * 1024; // 2 MB.

	private static long processedPageCounter = 0L;

	public DBReader() {
	}

	public SimilarityFilter getFilter() {
		return filter;
	}

	public void setFilter(SimilarityFilter filter) {
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
		this.filter = null; // GC Friendly
		Statement st = conn.createStatement();
		st.execute("SHUTDOWN");
		conn.close();
	}

	public void readDB() throws SQLException, ClassNotFoundException {
		connect();
		Statement st = null;
		ResultSet rs = null;
		st = conn.createStatement();
		String query = null;

		if (PAGES_TO_READ == -1) {
			query = "SELECT url,title,content FROM pages";
		} else {
			query = "SELECT LIMIT 0 " + PAGES_TO_READ + " url,title,content FROM pages";
		}

		rs = st.executeQuery(query);

		BlockingQueue<Page> queue = new LinkedBlockingDeque<>(200);
		final int threads = 4;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(200));

		for (int i = 0; i < threads; i++) {
			executor.execute(new PageProcessor(queue));
		}

		while (rs.next()) {

			String content;
			Page page = null;
			try {
				Clob clob = rs.getClob(3);
				String url = rs.getString(1);
				if (clob.length() < MAX_CLOB_SIZE) {

					String title = rs.getString(2);
					content = IOUtils.toString(clob.getCharacterStream());
					page = new Page(url, title, content);

					queue.put(page);
				} else {
					log.info("Skipping {} with size {}", url, clob.length());
				}
			} catch (IOException e) {
				log.error("Page {} could not be processed: {}", page.url, e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		st.close();

		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(2)); // Some time for the
														// threads to finish.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 1 minute
	}

	private class Page {
		private String url;
		private String title;
		private String content;

		public Page(String url, String title, String content) {
			this.url = url;
			this.title = title;
			this.content = content;
		}
	}

	private class PageProcessor implements Runnable {

		private BlockingQueue<Page> queue;

		public PageProcessor(BlockingQueue<Page> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			while (true) {
				Page page = null;
				try {
					page = queue.take();

					passPage(page.url, page.title, page.content);
					processedPageCounter++;
					if ((processedPageCounter % 1000) == 0) {
						console.info("{} pages read.", processedPageCounter);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					log.error("Page {} could not be processed: {}", page.url, e.getMessage());
				}
			}
		}

	}

	private boolean passPage(String url, String title, String content) throws IOException {
		String preProcessedContent = this.preProcessContent(content);
		if (!filter.isDuplicate(url, preProcessedContent)) {
			tokenizer.tokenize(url, title, content);
			return true;
		} else {
			dupsLogger.info("Duplicate: [{}]", url);
			return false;
		}
	}

	private String preProcessContent(String content) {
		Document doc = Jsoup.parse(content);

		Element body = doc.body();
		if (body != null) {
			return doc.body().text();
		} else {
			return "";
		}
	}
}