package edu.uci.ics.inf225.searchengine.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

public class SearchEngineLauncher {

	public static void main(String[] args) throws IOException, QueryException {
		BasicSearchEngine searchEngine = new BasicSearchEngine();

		System.out.println("Welcome to Search Engine.\n");

		System.out.print("Starting Search Engine...");
		searchEngine.start();
		System.out.println("Done.\n");

		String query = null;
		do {
			System.out.print("Query: ");
			query = readConsole();

			if (!StringUtils.isEmpty(query)) {
				System.out.println("Searching [" + query + "]\n");
			}
			QueryResult queryResult = searchEngine.query(query);
			printResult(queryResult);
		} while (!StringUtils.isEmpty(query));
		searchEngine.shutdown();
		System.out.println("Bye!");
	}

	private static void printResult(QueryResult queryResult) {
		System.out.println("Showing " + queryResult.size() + " of " + queryResult.getTotalPages());
		Iterator<QueryResultEntry> iterator = queryResult.iterator();

		while (iterator.hasNext()) {
			QueryResultEntry entry = iterator.next();
			System.out.println(entry.getUrl());
			System.out.println("[" + entry.getTitle() + "]");
			System.out.println("[" + entry.getContent() + "]");
			System.out.println("--------------------------------");

		}
		System.out.println("Query took " + queryResult.getExecutionTime() + " ms.");
	}

	private static String readConsole() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return br.readLine();
	}
}
