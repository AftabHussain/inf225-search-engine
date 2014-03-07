package edu.uci.ics.inf225.searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLUtils {

	/**
	 * Retrieves the body of an HTML page.
	 * 
	 * @param html
	 *            The HTML where the body will be parsed from.
	 * @return A String with the body's text.
	 */
	public static String extractBody(String html) {
		Document doc = Jsoup.parse(html);

		Element body = doc.body();
		if (body != null) {
			return doc.body().text();
		} else {
			return "";
		}
	}
}
