package edu.uci.ics.inf225.searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HTMLUtils {

	public static String parse(String html) {
		Document doc = Jsoup.parse(html);

		Element body = doc.body();
		if (body != null) {
			return doc.body().text();
		} else {
			return "";
		}
	}
}
