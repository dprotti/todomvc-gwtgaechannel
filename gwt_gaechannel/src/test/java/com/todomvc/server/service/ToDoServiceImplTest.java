package com.todomvc.server.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.todomvc.shared.model.ToDo;

public class ToDoServiceImplTest {

    private ToDoServiceImpl service;

    @Before
    public void setUp() {
        service = new ToDoServiceImpl();
    }

    @Test
    public void testGetSingletonList() {
        assertNotNull(service.getSingletonList());
    }

    @Test
    public void testAddAndGetToDo() {
        ToDo toDo1 = new ToDo("1", "todo1", false);
        ToDo toDo2 = new ToDo("2", "todo2", false);

        service.addToDo(service.getSingletonList().getId(), toDo1);
        service.addToDo(service.getSingletonList().getId(), toDo2);

        assertEquals(toDo1, service.getToDo("1"));
        assertEquals(toDo2, service.getToDo("2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToDoOnlyAddsToTheUniqueList() {
        ToDo toDo = new ToDo("1", "todo1", false);

        service.addToDo("someOtherList", toDo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetToDoWithUnknownIdFails() {
        service.getToDo("unknown");
    }

    @Test
    public void testNewToDo() {
        ToDo todo3 = service.newToDo("todo3", true);

        assertNotNull(todo3);
        assertNotNull(todo3.getId());
        assertEquals("todo3", todo3.getTitle());
        assertEquals(true, todo3.isCompleted());
    }

}
