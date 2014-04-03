package com.todomvc.client.command.todo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.Event;
import com.todomvc.client.ModelCache;
import com.todomvc.client.command.todo.ClientToDoListCommandExecutor;
import com.todomvc.client.events.ToDoListAddOrRemoveEvent;
import com.todomvc.client.events.ToDoListUpdatedEvent;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.SetCompletedToDoListCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

public class ClientToDoListCommandExecutorTest {

    @Mock private ModelCache modelCache;
    @Mock private EventBus eventBus;
    @Mock private Command<ToDoList> command;

    private ToDoList list;
    private ToDo task;
    private ClientToDoListCommandExecutor executor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        executor = new ClientToDoListCommandExecutor(eventBus, modelCache);
        list = new ToDoList("todos");
        when(modelCache.getToDoList()).thenReturn(list);
        task = new ToDo("1", "test todo", false);
    }

    @Test
    public void testExecuteAddCommand() {
        AddOrRemoveToDoCommand addCommand = new AddOrRemoveToDoCommand(list.getId(), task, true);

        Command<ToDoList> returnedCommand = executor.execute(addCommand);

        assertNotNull(returnedCommand);
        // check that an add event was fired notifying the addition of the task to the list
        ToDoListAddOrRemoveEvent addEvent =
                verifyFireEventAndReturnEvent(ToDoListAddOrRemoveEvent.class);
        assertTrue(addEvent.isAddition());
        assertEquals(list, addEvent.getToDoList());
        assertTrue(list.contains(task));
        assertEquals(task, addEvent.getAddedElement());
    }

    @Test
    public void testExecuteRemoveCommand() {
        list.add(task);
        AddOrRemoveToDoCommand removeCommand = new AddOrRemoveToDoCommand(list.getId(), task, false);

        Command<ToDoList> returnedCommand = executor.execute(removeCommand);

        assertNotNull(returnedCommand);
        // check that an add event was fired notifying the removal of the task to the list
        ToDoListAddOrRemoveEvent addEvent =
                verifyFireEventAndReturnEvent(ToDoListAddOrRemoveEvent.class);
        assertFalse(addEvent.isAddition());
        assertEquals(list, addEvent.getToDoList());
        assertFalse(list.contains(task));
        assertEquals(task, addEvent.getRemovedElement());
    }

    @Test
    public void testExecuteRemoveCommandProceedsOnlyForContainedTasks() {
        // 'task' is not in 'list', so executor should not execute the command
        AddOrRemoveToDoCommand removeCommand = new AddOrRemoveToDoCommand(list.getId(), task, false);

        Command<ToDoList> returnedCommand = executor.execute(removeCommand);

        assertNotNull(returnedCommand);
        verify(eventBus, never()).fireEvent(any(ToDoListAddOrRemoveEvent.class));
        assertFalse(list.contains(task));
    }

    @Test
    public void testExecuteSetCompletedCommand() {
        SetCompletedToDoListCommand command = new SetCompletedToDoListCommand(list, true);

        Command<ToDoList> returnedCommand = executor.execute(command);

        assertNotNull(returnedCommand);
        ToDoListUpdatedEvent updateEvent = verifyFireEventAndReturnEvent(ToDoListUpdatedEvent.class);
        assertEquals(list, updateEvent.getToDoList());
    }

    @Test
    public void testDidNotExecute() {
        when(command.canExecuteOn(list)).thenReturn(false);

        Command<ToDoList> returnedCommand = executor.execute(command);

        assertNotNull(returnedCommand);
        verify(command).canExecuteOn(list);
        verify(command, never()).execute(list);
        verify(eventBus, never()).fireEvent(any(Event.class));
    }

    private <E extends GwtEvent<?>> E verifyFireEventAndReturnEvent(Class<E> klass) {
        ArgumentCaptor<E> eventCaptor = ArgumentCaptor.forClass(klass);
        verify(eventBus).fireEvent(eventCaptor.capture());
        return eventCaptor.getValue();
    }

}
