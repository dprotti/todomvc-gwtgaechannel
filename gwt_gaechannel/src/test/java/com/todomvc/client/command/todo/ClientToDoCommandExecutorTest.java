package com.todomvc.client.command.todo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.event.shared.EventBus;
import com.google.web.bindery.event.shared.Event;
import com.todomvc.client.ModelCache;
import com.todomvc.client.command.todo.ClientToDoCommandExecutor;
import com.todomvc.client.events.ToDoUpdatedEvent;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.model.ToDo;

public class ClientToDoCommandExecutorTest {

    @Mock private ModelCache modelCache;
    @Mock private EventBus eventBus;
    @Mock private Command<ToDo> command;

    private ToDo task;
    private ClientToDoCommandExecutor executor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        executor = new ClientToDoCommandExecutor(modelCache, eventBus);
        task = new ToDo("1", "todo1", false);
        when(command.getTargetId()).thenReturn(task.getId());
    }

    @Test
    public void testExecute() {
        when(modelCache.getToDo(task.getId())).thenReturn(task);
        when(command.canExecuteOn(task)).thenReturn(true);

        Command<ToDo> returnedCommand = executor.execute(command);

        assertNotNull(returnedCommand);
        verify(command).canExecuteOn(task);
        verify(command).execute(task);
        // verify that an update event was fired for the target task
        ArgumentCaptor<ToDoUpdatedEvent> captor = ArgumentCaptor.forClass(ToDoUpdatedEvent.class);
        verify(eventBus).fireEvent(captor.capture());
        assertEquals(task, captor.getValue().getToDo());
    }

    @Test
    public void testDidNotExecute() {
        when(modelCache.getToDo(task.getId())).thenReturn(task);
        when(command.canExecuteOn(task)).thenReturn(false);

        Command<ToDo> returnedCommand = executor.execute(command);

        assertNotNull(returnedCommand);
        verify(command).canExecuteOn(task);
        verify(command, never()).execute(task);
        verify(eventBus, never()).fireEvent(any(Event.class));
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteThrowsRuntimeExceptionForUnknownTargetObject() {
        // no matter which task is requested, model cache does not recognize it
        when(modelCache.getToDo(anyString())).thenReturn(null);

        executor.execute(command);
    }

}
