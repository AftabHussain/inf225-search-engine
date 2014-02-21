package edu.uci.ics.inf225.searchengine.similarity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class DuplicateFilterTest {

	private static final String URL = "http://www.ics.uci.edu";

	@Test
	public void testIsDuplicate() {

		DuplicateFilter df = new DuplicateFilter();
		Assert.assertEquals("Failed to determine page's uniqueness", false, df.isDuplicate(URL, "aftab"));

		String str1 = getContents("1.txt");
		String str2 = getContents("2.txt");

		Assert.assertEquals("Failed to determine page's uniqueness", true, df.isDuplicate(URL, "aftab"));
		Assert.assertEquals("Failed to determine page's uniqueness", false, df.isDuplicate(URL, "hussain"));
		Assert.assertEquals("Failed to determine page's uniqueness", true, df.isDuplicate(URL, "hussain"));
		Assert.assertEquals("Failed to determine page's uniqueness", false, df.isDuplicate(URL, "aftaaaaaaaaaaaaaaaaaaaaaaaaab"));
		Assert.assertEquals("Failed to determine page's uniqueness", true, df.isDuplicate(URL, "aftaaaaaaaaaaaaaaaaaaaaaaaaabn"));
		Assert.assertEquals("Failed to determine page's uniqueness", false, df.isDuplicate(URL, str1));
		Assert.assertEquals("Failed to determine page's uniqueness", true, df.isDuplicate(URL, str2));

	}

	private String getContents(String filename) {
		try {
			return FileUtils.readFileToString(new File("src/test/resources/" + filename));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}
}