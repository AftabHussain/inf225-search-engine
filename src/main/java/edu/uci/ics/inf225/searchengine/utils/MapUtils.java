package edu.uci.ics.inf225.searchengine.utils;

import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {

	public static <K, V> boolean mapsAreEqual(Map<K, V> map1, Map<K, V> map2) {

		if ((map1 == map2) && map1 == null) {
			return true;
		}

		if (map1 == null || map2 == null) { // Both are not null at the same
											// time for previous condition.
			return false;
		}

		if (map1.size() != map2.size()) {
			return false;
		}

		for (Entry<K, V> entry : map1.entrySet()) {
			if (!map2.get(entry.getKey()).equals(entry.getValue())) {
				return false;
			}
		}
		return true;
	}
}
