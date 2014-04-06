/*!! client.command.todo */
package com.todomvc.client.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.google.gwt.event.shared.EventBus;
import com.todomvc.client.ModelCache;
import com.todomvc.client.events.ToDoListAddOrRemoveEvent;
import com.todomvc.client.events.ToDoListUpdatedEvent;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Executes on the client the commands acting on {@link ToDoList}'s and publishes the
 * changes on the global <a href=
 * "http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html"
 * >EventBus</a>.
 */
/*!
  Executes on the client the commands acting on
  [ToDoList](${basePath}/java/com/todomvc/shared/model/ToDoList.java.html)'s and
  publishes the changes on the global <a href=
  [EventBus](http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html).
 */
public class ClientToDoListCommandExecutor implements ToDoListCommandExecutor {

    private final EventBus eventBus;
    private final ModelCache modelCache;

    @Inject
    public ClientToDoListCommandExecutor(EventBus eventBus, ModelCache modelCache) {
        this.eventBus = checkNotNull(eventBus);
        this.modelCache = checkNotNull(modelCache);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This client implementation executes the command and triggers the following events on the global
     * <a href="http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html">EventBus</a>:
     *
     * <ul>
     *   <li>{@link ToDoListAddOrRemoveEvent} if {@code command} is an {@link AddOrRemoveToDoCommand}</li>
     *   <li>{@link ToDoListUpdatedEvent} otherwise</li>
     * </ul>
     * </p>
     */
    /*!
      This client implementation executes the command and triggers the following events on the global
      [EventBus](http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html):
     
      - [ToDoListAddOrRemoveEvent](${basePath}/java/com/todomvc/client/events/ToDoListAddOrRemoveEvent.java.html)
        if command is
        an [AddOrRemoveToDoCommand](${basePath}/java/com/todomvc/shared/command/todo/AddOrRemoveToDoCommand.java.html)
      - [ToDoListUpdatedEvent](${basePath}/java/com/todomvc/client/events/ToDoListUpdatedEvent.java.html) otherwise
     */
    @Override
    public Command<ToDoList> execute(Command<ToDoList> command) {
        checkNotNull(command);
        ToDoList toDos = modelCache.getToDoList();
        if (command.canExecuteOn(toDos)) {
            command.execute(toDos);
            emitEvents(toDos, command);
        }
        return command;
    }

    private void emitEvents(ToDoList toDos, Command<ToDoList> command) {
    	if (command instanceof AddOrRemoveToDoCommand) {
            AddOrRemoveToDoCommand addOrRemoveCommand = (AddOrRemoveToDoCommand) command;
            boolean isAddition = addOrRemoveCommand.isAddition();
            ToDo toDo = isAddition
                    ? addOrRemoveCommand.getAddedToDo()
                    : addOrRemoveCommand.getRemovedToDo();
            ToDoListAddOrRemoveEvent addEvent =
                    new ToDoListAddOrRemoveEvent(toDos, toDo, isAddition);
            eventBus.fireEvent(addEvent);
            return;
        }
    	eventBus.fireEvent(new ToDoListUpdatedEvent(toDos));
    }

}
