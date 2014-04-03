/*!! client */
package com.todomvc.client;

import com.todomvc.shared.model.ToDo;

public enum ToDoRouting {
	ALL(new MatchAll()),
	ACTIVE(new MatchActive()),
	COMPLETED(new MatchCompleted());

	/**
	 * Matcher used to filter todo items, based on some criteria.
	 */
	public interface Matcher {
		/**
		 * Determines whether the given todo item meets the criteria of this matcher.
		 */
		boolean matches(ToDo item);
	}

	/**
	 * A matcher that matches any todo item.
	 */
	private static class MatchAll implements Matcher {
		@Override
		public boolean matches(ToDo item) {
			return true;
		}
	}

	/**
	 * A matcher that matches only active todo items.
	 */
	private static class MatchActive implements Matcher {
		@Override
		public boolean matches(ToDo item) {
			return !item.isCompleted();
		}
	}

	/**
	 * A matcher that matches only completed todo items.
	 */
	private static class MatchCompleted implements Matcher {
		@Override
		public boolean matches(ToDo item) {
			return item.isCompleted();
		}
	}

	private final Matcher matcher;

	private ToDoRouting(Matcher matcher) {
		this.matcher = matcher;
	}

	public Matcher getMatcher() {
		return matcher;
	}

}
