package com.todomvc.shared.command.todo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

public class RemoveCompletedToDoListCommandTest {

    private ToDoList toDos;

    @Before
    public void setUp() {
        toDos = new ToDoList("todos");
    }

    @Test
    public void testExecute() {
        ToDo task1 = new ToDo("1", "todo 1", true); // task1 completed
        ToDo task2 = new ToDo("2", "todo 2", true); // task2 completed
        ToDo task3 = new ToDo("3", "todo 3", false);
        toDos.addAll(Arrays.asList(task1, task2, task3));
        RemoveCompletedToDoListCommand command = new RemoveCompletedToDoListCommand(toDos);
        // after the command was created...
        task2.setCompleted(false);  // task2 is not completed anymore
        task3.setCompleted(true);   // task3 is now completed (but it wasn't when command was created)
        
        command.execute(toDos);

        // task1 was requested to be removed and had 'completed' status on execute()
        assertFalse(toDos.contains(task1));
        // task2 was requested to be removed but then became active before execute()
        assertTrue(toDos.contains(task2));
        // task3 was not requested to be removed
        assertTrue(toDos.contains(task3));
    }

}
