package com.teaminabox.eclipse.wiki.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.*;

public final class HistoryTest {

	private History<String>	history	= new History<String>();

	@Test
	public void testAdd() {
		history.add("foo");
		assertEquals("size", 1, history.size());
		assertEquals("location", 0, history.getLocation());
	}

	@Test
	public void testAddingTheSameThingTwice() {
		history.add("foo");
		history.add("foo");
		assertEquals("size", 1, history.size());
		assertEquals("location", 0, history.getLocation());
	}

	@Test
	public void testPrevious() {
		history.add("foo");
		history.add("bar");
		assertEquals("foo", history.back());
	}

	@Test
	public void testHasPrevious() {
		assertFalse("nothing", history.hasPrevious());
		history.add("foo");
		assertFalse("one", history.hasPrevious());
	}

	@Test
	public void testPreviousNothingAdded() {
		assertEquals("location", -1, history.getLocation());
		assertFalse("hasPrevious", history.hasPrevious());
	}

	@Test
	public void testNext() {
		history.add("foo");
		history.add("bar");
		history.back();
		assertEquals("bar", history.next());
	}

	@Test
	public void testNextNothingAdded() {
		assertEquals("location", -1, history.getLocation());
		assertFalse("hasNext", history.hasNext());
	}

	@Test
	public void testFutureCleared() {
		history.add("foo");
		history.add("bar");
		history.add("doomed");
		history.back();
		history.back();
		history.add("newStuff");

		assertEquals("size", 2, history.size());
		assertEquals("newStuff", history.getCurrent());
	}
}
