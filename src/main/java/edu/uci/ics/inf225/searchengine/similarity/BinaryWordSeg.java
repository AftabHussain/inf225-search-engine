package edu.uci.ics.inf225.searchengine.similarity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Code taken from https://github.com/sing1ee/simhash-java.
 * 
 */
public class BinaryWordSeg implements IWordSeg {

	@Override
	public List<String> tokens(String doc) {
		return new TwoGramStringProxy(doc);
	}

	private class TwoGramStringProxy implements List<String> {

		private String originalString;

		public TwoGramStringProxy(String originalString) {
			this.originalString = originalString;
		}

		@Override
		public int size() {
			return this.originalString.length() - 1;
		}

		@Override
		public boolean isEmpty() {
			return this.originalString.length() <= 1;
		}

		@Override
		public boolean contains(Object o) {
			String containedString = o.toString();
			if (!isTwoGram(containedString)) {
				return false;
			}
			return this.originalString.contains(containedString);
		}

		private boolean isTwoGram(String containedString) {
			return containedString.length() != 2;
		}

		@Override
		public Iterator<String> iterator() {
			return new TwoGramStringProxyIterator(this);
		}

		private String[] createActualArray(String[] array) {
			for (int i = 0; i < this.originalString.length() - 1; i++) {
				array[0] = this.originalString.substring(i, i + 2);
			}
			return array;
		}

		private String[] createActualArray() {
			return this.createActualArray(new String[this.originalString.length() - 1]);
		}

		@Override
		public Object[] toArray() {
			return this.createActualArray();
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a instanceof String[]) {
				return (T[]) this.createActualArray((String[]) a);
			} else {
				return (T[]) this.createActualArray();
			}
		}

		@Override
		public boolean add(String e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object object : c) {
				if (!this.contains(object)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends String> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String get(int index) {
			return this.originalString.substring(index, index + 2);
		}

		@Override
		public String set(int index, String element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, String element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int lastIndexOf(Object o) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ListIterator<String> listIterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListIterator<String> listIterator(int index) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> subList(int fromIndex, int toIndex) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class TwoGramStringProxyIterator implements Iterator<String> {

		private TwoGramStringProxy list;

		private int i = 0;

		public TwoGramStringProxyIterator(TwoGramStringProxy list) {
			this.list = list;
		}

		@Override
		public boolean hasNext() {
			return i < list.size();
		}

		@Override
		public String next() {
			return list.get(i++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public List<String> tokens(String doc, Set<String> stopWords) {
		return null;
	}

}
