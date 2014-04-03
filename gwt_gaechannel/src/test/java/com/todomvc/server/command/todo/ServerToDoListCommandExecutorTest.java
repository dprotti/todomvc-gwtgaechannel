package com.todomvc.server.command.todo;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.SetCompletedToDoListCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoService;

public class ServerToDoListCommandExecutorTest {

    @Mock private ToDoService toDoService;

    private ToDoList toDos;
    private ServerToDoListCommandExecutor executor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        toDos = new ToDoList("test todos");
        when(toDoService.getSingletonList()).thenReturn(toDos);
        executor = new ServerToDoListCommandExecutor(toDoService);
    }

    @Test
    public void testExecuteAddCommand() {
        ToDo taskWithoutId = new ToDo(null, "test todo", false);
        ToDo taskWithId = new ToDo("1", "test todo", false);
        when(toDoService.newToDo(taskWithoutId.getTitle(), taskWithoutId.isCompleted()))
                .thenReturn(taskWithId);
        AddOrRemoveToDoCommand addCommand =
                new AddOrRemoveToDoCommand(toDos.getId(), taskWithoutId, true);

        AddOrRemoveToDoCommand returnedCommand =
                (AddOrRemoveToDoCommand) executor.execute(addCommand);

        assertNotNull(returnedCommand);
        assertEquals(toDos.getId(), returnedCommand.getTargetId());
        assertTrue(returnedCommand.isAddition());
        assertEquals(taskWithId, returnedCommand.getAddedToDo());
        verify(toDoService).addToDo(toDos.getId(), taskWithId);
    }

    @Test
    public void testExecuteRemoveCommand() {
        ToDo task = new ToDo("1", "test todo", false);
        toDos.add(task);
        AddOrRemoveToDoCommand addCommand =
                new AddOrRemoveToDoCommand(toDos.getId(), task, false);

        AddOrRemoveToDoCommand returnedCommand =
                (AddOrRemoveToDoCommand) executor.execute(addCommand);

        assertNotNull(returnedCommand);
        assertEquals(toDos.getId(), returnedCommand.getTargetId());
        assertFalse(returnedCommand.isAddition());
        assertEquals(task, returnedCommand.getRemovedToDo());
        // now the server side list lives in-memory, no need to invoke a service.
        // if in the future the list is persisted, some ToDoService method should
        // be invoked to persist the change to the list.
        //verify(toDoService).removeToDo(toDos.getId(), task.getId());
        assertFalse(toDos.contains(task));
    }

    @Test
    public void testExecuteSetCompletedCommand() {
        ToDo task1 = new ToDo("1", "todo 1", false);
        ToDo task2 = new ToDo("2", "todo 2", false);
        toDos.add(task1);
        toDos.add(task2);
        SetCompletedToDoListCommand command =
                new SetCompletedToDoListCommand(toDos, true);

        SetCompletedToDoListCommand returnedCommand =
                (SetCompletedToDoListCommand) executor.execute(command);

        assertNotNull(returnedCommand);
        assertEquals(toDos.getId(), returnedCommand.getTargetId());
        assertTrue(returnedCommand.getCompleted());
        assertTrue(toDos.contains(task1));
        assertTrue(toDos.contains(task2));
        for (ToDo task : toDos) {
            assertTrue(task.isCompleted());
        }
    }

}
