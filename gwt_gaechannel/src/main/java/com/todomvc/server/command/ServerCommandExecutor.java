/*!! server.command */
package com.todomvc.server.command;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.Maps;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;

/**
 * Server-side delegating command executor.
 *
 * It delegates execution of commands to an appropriate executor based on the type
 * of the command passed to {@link #execute(Command)}. 
 *
 * @author Duilio Protti
 */
/*!
  Server-side implementation of a delegating
  [CommandExecutor](${basePath}/java/com/todomvc/shared/command/CommandExecutor.java.html).
  It delegates execution of commands to an appropriate executor based on the type
  of the command passed to `execute(Command)`. See for example the server-side
  [ServerToDoCommandExecutor](${basePath}/java/com/todomvc/server/command/todo/ServerToDoCommandExecutor.java.html).

  There is a corresponding
  [client-side implementation](${basePath}/java/com/todomvc/client/command/ClientCommandExecutor.java.html).
  */
@Singleton
@SuppressWarnings("rawtypes")
public class ServerCommandExecutor implements CommandExecutor {

    private final Map<String, CommandExecutor> executors = Maps.newHashMap();

    @Inject
    public ServerCommandExecutor(ToDoListCommandExecutor toDoListExecutor, ToDoCommandExecutor toDoExecutor) {
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
