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

/**
 * Executes on the client the commands acting on {@link ToDo}'s and publishes
 * {@link ToDoUpdatedEvent}'s on the global <a href=
 * "http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html"
 * >EventBus</a>.
 */
/*!
  Executes on the client the commands acting on
  [ToDo](${basePath}/java/com/todomvc/shared/model/ToDo.java.html)'s and
  publishes `ToDoUpdatedEvent`'s on the global <a href=
  [EventBus](http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html).
 */
public class ClientToDoCommandExecutor implements ToDoCommandExecutor {

    private final ModelCache modelCache;
    private final EventBus eventBus;

    @Inject
    public ClientToDoCommandExecutor(ModelCache modelCache, EventBus eventBus) {
        this.modelCache = checkNotNull(modelCache);
        this.eventBus = checkNotNull(eventBus);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This client implementation executes the command and triggers a
     * {@link ToDoUpdatedEvent} on the global <a href=
     * "http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html"
     * >EventBus</a>.
     * </p>
     */
    /*!
      This client implementation executes the command and triggers a `ToDoUpdatedEvent` on the
      global
      [EventBus](http://www.gwtproject.org/javadoc/latest/com/google/gwt/event/shared/EventBus.html):
     */
    @Override
    public Command<ToDo> execute(Command<ToDo> command) {
        checkNotNull(command);
        ToDo task = modelCache.getToDo(command.getTargetId());
        if (task == null) {
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
