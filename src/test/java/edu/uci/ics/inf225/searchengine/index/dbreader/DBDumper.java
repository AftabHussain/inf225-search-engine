package edu.uci.ics.inf225.searchengine.index.dbreader;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.inf225.searchengine.dbreader.DBReader;
import edu.uci.ics.inf225.searchengine.dbreader.WebPage;

/**
 * @author Matias Giorgio.
 * 
 */
public class DBDumper {

	private static final Logger log = LoggerFactory.getLogger(DBDumper.class);

	private DBReader dbReader;

	public DBDumper() {
	}

	@Before
	public void setup() throws SQLException, ClassNotFoundException {
		dbReader = new DBReader();
		dbReader.start();
	}

	@After
	public void tearDown() throws SQLException {
		dbReader.shutdown();
	}

	@Test
	public void testDBIterator() throws SQLException {
		int expectedSize = dbReader.count();
		System.out.println("Expecting " + expectedSize + " pages.");
		Iterator<WebPage> iterator = dbReader.iterator();

		int counter = 0;

		while (iterator.hasNext()) {
			counter++;
			iterator.next();
		}

		System.out.println("Found " + counter + " pages.");

		Assert.assertEquals("Iterated number of pages is wrong", expectedSize, counter);
	}

	public void dumpPagesTable() throws SQLException, IOException {
		FileWriter writer = new FileWriter("dbdump.txt");

		writer.write("Number of URLs: ");
		writer.write(String.valueOf(dbReader.count()));
		writer.write("\n");

		Iterator<WebPage> iterator = dbReader.iterator();

		while (iterator.hasNext()) {
			WebPage page = iterator.next();

			writer.write(page.getUrl());
			writer.write("\n");
		}

		writer.close();
	}
}