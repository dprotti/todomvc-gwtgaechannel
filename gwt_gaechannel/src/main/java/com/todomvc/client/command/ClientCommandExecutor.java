/*!! client.command */
package com.todomvc.client.command;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;

/**
 * Client-side delegating command executor.
 *
 * It delegates execution of commands to an appropriate executor based on the type
 * of the command passed to {@link #execute(Command)}. 
 *
 * @author Duilio Protti
 */
/*!
  Client-side implementation of a delegating
  [CommandExecutor](${basePath}/java/com/todomvc/shared/command/CommandExecutor.java.html).
  It delegates execution of commands to an appropriate executor based on the type
  of the command passed to `execute(Command)`. See for example the client-side
  [ClientToDoCommandExecutor](${basePath}/java/com/todomvc/client/command/todo/ClientToDoCommandExecutor.java.html).

  There is a corresponding
  [server-side implementation](${basePath}/java/com/todomvc/server/command/ServerCommandExecutor.java.html).
 */
@SuppressWarnings("rawtypes")
public class ClientCommandExecutor implements CommandExecutor {

    private final Map<String, CommandExecutor> executors = Maps.newHashMap();

    @Inject
    public ClientCommandExecutor(ToDoListCommandExecutor toDoListExecutor, ToDoCommandExecutor toDoExecutor) {
        executors.put(ToDoListCommandExecutor.TYPE, checkNotNull(toDoListExecutor));
        executors.put(ToDoCommandExecutor.TYPE, checkNotNull(toDoExecutor));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Command execute(Command command) {
        CommandExecutor executor = executors.get(command.getCommandType());
        checkState(executor != null,
                "there is no executor for command of type " + command.getCommandType());
        return executor.execute(command);
    }

}
