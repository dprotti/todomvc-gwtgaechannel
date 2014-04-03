package com.todomvc.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.todomvc.client.command.CommandController;
import com.todomvc.client.events.ToDoListAddOrRemoveEvent;
import com.todomvc.client.events.ToDoListUpdatedEvent;
import com.todomvc.client.events.ToDoUpdatedEvent;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.SetCompletedToDoListCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoServiceAsync;

public class ToDoPresenterTest {

    @Mock private ToDoServiceAsync toDoService;
    @Mock private CommandController commandController;
    @Mock private EventBus eventBus;
    @Mock private Historian historian;

    @Mock private ToDoPresenter.Display view;

    // TODO define and use an instrumented subclass of ToDoList allowing for capture and
    // assertion of operations performed on the list by the presenter.
    private ToDoList toDos;
    private ToDoPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(historian.getToken()).thenReturn("");
        toDos = new ToDoList("todos");
        presenter = new ToDoPresenter(commandController, toDoService, eventBus, historian);
        presenter.setToDoList(toDos);
    }

    @Test
    public void testThatListenForToDoUpdates() {
        presenter.bindView(view);

        verify(eventBus).addHandler(eq(ToDoUpdatedEvent.TYPE), any(ToDoUpdatedEvent.Handler.class));
        
        // TODO capture handler and test that statistics are updated on to-do status change
    }

    // TODO test filters

//  private <E extends EventHandler> E verifyAddHandlerAndReturnHandler(Event.Type<E> eventType, Class<E> klass) {
//      ArgumentCaptor<E> handlerCaptor = ArgumentCaptor.forClass(klass);
//      verify(eventBus).addHandler(eq(eventType), handlerCaptor.capture());
//      return handlerCaptor.getValue();
//  }

    @Test
    public void testThatListenForToDoListUpdatedEvent() {
//        toDos.add(new ToDo("1", "todo 1", false));
//        toDos.add(new ToDo("2", "todo 2", false));
        presenter.bindView(view);

        presenter.initListEditingAndConnectHandlers(toDos);

        verify(eventBus).addHandler(eq(ToDoListUpdatedEvent.TYPE), any(ToDoListUpdatedEvent.Handler.class));

        // TODO check that updates statistics
//        ToDoListUpdatedEvent.Handler handler =
//                verifyAddHandlerAndReturnHandler(ToDoListUpdatedEvent.TYPE, ToDoListUpdatedEvent.Handler.class);
//
//        for (ToDo task : toDos) {
//            task.setCompleted(true);
//        }
//        ToDoListUpdatedEvent event = new ToDoListUpdatedEvent(toDos);
//
//        handler.onEvent(event);
//        ...
    }

    @Test
    public void testThatListenForToDoListAddOrRemoveEvent() {
        presenter.bindView(view);

        presenter.initListEditingAndConnectHandlers(toDos);

        verify(eventBus).addHandler(eq(ToDoListAddOrRemoveEvent.TYPE), any(ToDoListAddOrRemoveEvent.Handler.class));
    }

    @Test(expected = NullPointerException.class)
    public void testBindViewRejectsNull() {
        presenter.bindView(null);
    }

    @Test
    public void testDisplayEventHandlerAddsTask() {
        ToDoPresenter.Display.EventHandler handler = bindViewAndCaptureDisplayHandler();
        // prepare data for handler
        when(view.getTaskText()).thenReturn("  todo 1   ");

        handler.addTask();

        // view must clear input text after user enters task title
        verify(view).clearTaskText();

        AddOrRemoveToDoCommand command = verifyAndCaptureExecutedCommand(AddOrRemoveToDoCommand.class);
        assertTrue(command.isAddition());
        // task title must be trimmed
        assertEquals("todo 1", command.getAddedToDo().getTitle());
    }

    @Test
    public void testDisplayEventHandlerMarksAllCompleted() {
        ToDoPresenter.Display.EventHandler handler = bindViewAndCaptureDisplayHandler();

        handler.markAllCompleted(true);

        SetCompletedToDoListCommand command =
                verifyAndCaptureExecutedCommand(SetCompletedToDoListCommand.class);
        assertEquals(toDos.getId(), command.getTargetId());
        assertEquals(true, command.getCompleted());
    }

    @Test
    public void testDisplayEventHandlerMarksAllNotCompleted() {
        ToDoPresenter.Display.EventHandler handler = bindViewAndCaptureDisplayHandler();

        handler.markAllCompleted(false);

        SetCompletedToDoListCommand command =
                verifyAndCaptureExecutedCommand(SetCompletedToDoListCommand.class);
        assertEquals(toDos.getId(), command.getTargetId());
        assertEquals(false, command.getCompleted());
    }

    @Test
    public void testDeleteTask() {
        ToDo task1 = new ToDo("1", "todo 1", false);
        ToDo task2 = new ToDo("2", "todo ", false);
        toDos.add(task1);
        toDos.add(task2);
        presenter.bindView(view);
        presenter.initListEditingAndConnectHandlers(toDos);

        presenter.deleteTask(task1);

        AddOrRemoveToDoCommand command =
                verifyAndCaptureExecutedCommand(AddOrRemoveToDoCommand.class);
        assertEquals(toDos.getId(), command.getTargetId());
        assertFalse(command.isAddition());
        assertEquals(task1, command.getRemovedToDo());
    }

    private ToDoPresenter.Display.EventHandler bindViewAndCaptureDisplayHandler() {
        ArgumentCaptor<ToDoPresenter.Display.EventHandler> handlerCaptor =
                ArgumentCaptor.forClass(ToDoPresenter.Display.EventHandler.class);
        presenter.bindView(view);
        verify(view).addhandler(handlerCaptor.capture());
        return checkNotNull(handlerCaptor.getValue());
    }

    // verifies that command of type E was executed, and if so it returns the command
    private <E extends Command<?>> E verifyAndCaptureExecutedCommand(Class<E> klass) {
        ArgumentCaptor<E> commandCaptor = ArgumentCaptor.forClass(klass);
        verify(commandController).executeCommand(commandCaptor.capture());
        return checkNotNull(commandCaptor.getValue());
    }
}

