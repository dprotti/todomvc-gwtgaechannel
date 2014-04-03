package com.todomvc.shared.model;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class ToDoTest {

    @Test
    public void testHasZeroArgCtor() {
        new ToDo();
    }

    @Test
    public void testSetId() {
        ToDo toDo = new ToDo(null, "todo1", false);
        toDo.setId("1");
        assertEquals("1", toDo.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void testSetIdFailsIfIdWasAlreadySet() {
        ToDo toDo = new ToDo("1", "todo1", false);
        toDo.setId("2");
    }

    @Test(expected = NullPointerException.class)
    public void testTitleCannotBeNull() {
        new ToDo("1", null, false);
    }

    @Test
    public void testSetTitle() {
        ToDo toDo = new ToDo("1", "todo1", false);
        toDo.setTitle("todo1 updated");
        assertEquals("todo1 updated", toDo.getTitle());
    }

    @Test(expected = NullPointerException.class)
    public void testSetTitleRejectsNull() {
        ToDo toDo = new ToDo("1", "todo1", false);
        toDo.setTitle(null);
    }

    @Test
    public void testSetCompleted() {
        ToDo toDo = new ToDo("1", "todo1", false);
        assertEquals(false, toDo.isCompleted());
        toDo.setCompleted(true);
        assertEquals(true, toDo.isCompleted());
    }

    @Test
    public void testEquals() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("1", "todo1", false);
        assertTrue(task1.equals(task2));
    }

    @Test
    public void testNotEquals() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("2", "todo1", false);
        assertFalse(task1.equals(task2));
    }

    @Test
    public void testHashCode() {
        ToDo task1 = new ToDo("1", "todo1", false);
        ToDo task2 = new ToDo("1", "todo1", false);
        ToDo task3 = new ToDo("3", "todo 3", false);
        assertEquals(task1.hashCode(), task2.hashCode());
        assertTrue(task1.hashCode() != task3.hashCode());
    }

}
