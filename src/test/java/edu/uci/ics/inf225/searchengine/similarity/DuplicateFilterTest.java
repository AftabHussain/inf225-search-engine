package edu.uci.ics.inf225.searchengine.similarity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class DuplicateFilterTest {

	@Test
	public void testIsDuplicate() {

		DuplicateFilter df = new DuplicateFilter();
		Assert.assertEquals("Failed to determine page's uniqueness", false,
				df.isDuplicate("aftab"));
		
		String str1= getContents("1.txt");
		String str2= getContents("2.txt");
		/*
		 * Assert.assertEquals("Failed to determine page's uniqueness", "aftab",
		 * df.uniquePageList);
		 */
	    System.out.println(1);
		Assert.assertEquals("Failed to determine page's uniqueness", true,
				df.isDuplicate("aftab"));
		System.out.println(2);
		Assert.assertEquals("Failed to determine page's uniqueness", false,
				df.isDuplicate("hussain"));
		System.out.println(3);
		Assert.assertEquals("Failed to determine page's uniqueness", true,
				df.isDuplicate("hussain"));
		System.out.println(4);
		Assert.assertEquals("Failed to determine page's uniqueness", false,
				df.isDuplicate("aftaaaaaaaaaaaaaaaaaaaaaaaaab"));
		System.out.println(5);
		Assert.assertEquals("Failed to determine page's uniqueness", true,
				df.isDuplicate("aftaaaaaaaaaaaaaaaaaaaaaaaaabn"));
		System.out.println(6);
		Assert.assertEquals("Failed to determine page's uniqueness", false,
				df.isDuplicate(str1));
		System.out.println(7);
		Assert.assertEquals("Failed to determine page's uniqueness", true,
				df.isDuplicate(str2));
		//System.out.println("asdf");
		
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