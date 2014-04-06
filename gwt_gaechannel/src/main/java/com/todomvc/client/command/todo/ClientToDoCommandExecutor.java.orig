/*!! client.command.todo */
package com.todomvc.client.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.todomvc.client.ModelCache;
import com.todomvc.client.events.ToDoUpdatedEvent;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.model.ToDo;

/*!
  Client-side
  [CommandExecutor](${basePath}/java/com/todomvc/shared/command/CommandExecutor.java.html)
  for commands acting on
  [ToDo](${basePath}/java/com/todomvc/shared/model/ToDo.java.html)'s.
 */
public class ClientToDoCommandExecutor implements ToDoCommandExecutor {

    private final ModelCache modelCache;
    private final EventBus eventBus;

    @Inject
    public ClientToDoCommandExecutor(ModelCache modelCache, EventBus eventBus) {
        this.modelCache = checkNotNull(modelCache);
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public Command<ToDo> execute(Command<ToDo> command) {
        checkNotNull(command);
        ToDo task = modelCache.getToDo(command.getTargetId());
        if (task == null) {
            // TODO should we define a runtime CommandExecutionException?
            throw new RuntimeException("Cannot proceed. Unknown target object with id "
                    + command.getTargetId());
        }
        if (command.canExecuteOn(task)) {
            command.execute(task);
            eventBus.fireEvent(new ToDoUpdatedEvent(task));
        }
        return command;
    }

}
