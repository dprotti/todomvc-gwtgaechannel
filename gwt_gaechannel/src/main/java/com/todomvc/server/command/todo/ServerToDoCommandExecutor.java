/*!! server.command.todo */
package com.todomvc.server.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.service.ToDoService;

/*!
  Server-side executor for
  [ToDoCommand](${basePath}/java/com/todomvc/shared/command/todo/ToDoCommand.java.html)'s.
*/
public class ServerToDoCommandExecutor implements ToDoCommandExecutor {

    private ToDoService toDoService;

    @Inject
    public ServerToDoCommandExecutor(ToDoService toDoService) {
        this.toDoService = checkNotNull(toDoService);
    }

    @Override
    public Command<ToDo> execute(Command<ToDo> command) {
        checkNotNull(command);
        ToDo toDo = toDoService.getToDo(command.getTargetId());
        if (command.canExecuteOn(toDo)) {
            command.execute(toDo);
        }
        return command;
    }
}
