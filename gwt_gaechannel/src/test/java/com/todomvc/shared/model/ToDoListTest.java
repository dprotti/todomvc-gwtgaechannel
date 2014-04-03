package com.todomvc.shared.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class ToDoListTest {

    private static final String LIST_ID = "todos";

    private ToDoList list;

    @Before
    public void setUp() {
        list = new ToDoList(LIST_ID);
    }

    @Test
    public void testGetId() {
        assertEquals(LIST_ID, list.getId());
    }

    @Test
    public void testGetToDo() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("2", "todo2", true);

        list.add(task1);
        list.add(task2);

        assertEquals(task1, list.getToDo(task1.getId()));
        assertEquals(task2, list.getToDo(task2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnknownToDo() {
        list.getToDo("unknown");
    }

    // TODO move the next four tests to IdentifiableCollectionTest
    @Test
    public void testRemove() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("2", "todo2", true);

        assertFalse(list.contains(task1));
        assertFalse(list.contains(task2));
        list.add(task1);
        assertTrue(list.contains(task1));
        list.add(task2);
        assertTrue(list.contains(task1));
        assertTrue(list.contains(task2));
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterationFollowsAdditionOrder() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("2", "todo2", true);
        ToDo task3 = new ToDo("3", "todo3", false);
        ToDo task4 = new ToDo("4", "todo4", true);

        list.addAll(Arrays.asList(task1, task2, task3, task4));

        assertEquals(4, list.size());
        Iterator<ToDo> it = list.iterator();
        assertEquals(task1, it.next());
        assertEquals(task2, it.next());
        assertEquals(task3, it.next());
        assertEquals(task4, it.next());
        it.next(); // there should be no more elements
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsDuplicates() {
        ToDo task1 = new ToDo("1", "todo1", false);
        list.add(task1);
        list.add(task1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorRemove() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("2", "todo2", true);
        ToDo task3 = new ToDo("3", "todo3", false);

        list.addAll(Arrays.asList(task1, task2, task3));

        Iterator<ToDo> it = list.iterator();
        assertEquals(task1, it.next());
        assertEquals(task2, it.next());
        it.remove();

        assertEquals(2, list.size());
        it = list.iterator();
        assertEquals(task1, it.next());
        assertEquals(task3, it.next());
        it.next(); // there should be no more elements
    }

}
