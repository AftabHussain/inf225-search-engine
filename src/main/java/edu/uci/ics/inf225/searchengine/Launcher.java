package edu.uci.ics.inf225.searchengine;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

	private static final Logger console = LoggerFactory.getLogger("console");

	public static void main(String[] args) {
		SearchEngineController controller = new SearchEngineController();

		try {
			controller.start();
			controller.shutdown();
		} catch (SQLException | ClassNotFoundException e) {
			console.error("DB could not be accessed: {}", e.getMessage());
		} catch (IOException e) {
			console.error("Problem occurred when shutting down: {}", e.getMessage());
		}
	}
}
