package com.todomvc.server.command.todo;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.todomvc.server.command.todo.ServerToDoCommandExecutor;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.service.ToDoService;

public class ServerToDoCommandExecutorTest {

    @Mock private ToDoService toDoService;
    @Mock private Command<ToDo> command;

    private ToDo task;
    private ServerToDoCommandExecutor executor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        executor = new ServerToDoCommandExecutor(toDoService);
        final String ID = "1";
        task = new ToDo(ID, "test todo", false);
        when(command.getTargetId()).thenReturn(ID);
        when(toDoService.getToDo(ID)).thenReturn(task);
    }

    @Test
    public void testExecute() {
        when(command.canExecuteOn(task)).thenReturn(true);

        Command<ToDo> returnedCommand = executor.execute(command);

        verify(command).canExecuteOn(task);
        verify(command).execute(task);
        assertNotNull(returnedCommand);
    }

    @Test
    public void testDidNotExecute() {
        when(command.canExecuteOn(task)).thenReturn(false);

        Command<ToDo> returnedCommand = executor.execute(command);

        verify(command).canExecuteOn(task);
        verify(command, never()).execute(task);
        assertNotNull(returnedCommand);
    }

}
