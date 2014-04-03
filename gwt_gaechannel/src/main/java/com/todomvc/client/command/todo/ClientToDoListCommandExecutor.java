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

/*!
  Client-side
  [CommandExecutor](${basePath}/java/com/todomvc/shared/command/CommandExecutor.java.html)
  for commands acting on
  [ToDoList](${basePath}/java/com/todomvc/shared/model/ToDoList.java.html)'s.
 */
public class ClientToDoListCommandExecutor implements ToDoListCommandExecutor {

    private final EventBus eventBus;
    private final ModelCache modelCache;

    @Inject
    public ClientToDoListCommandExecutor(EventBus eventBus, ModelCache modelCache) {
        this.eventBus = checkNotNull(eventBus);
        this.modelCache = checkNotNull(modelCache);
    }

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
