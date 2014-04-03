/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.model.ToDo;

/**
 * Executor for commands acting on to-do items.
 *
 * @author Duilio Protti
 */
/*! Executor for commands acting on to-do items. */
public interface ToDoCommandExecutor extends CommandExecutor<Command<ToDo>> {

    public static String TYPE = ToDoCommandExecutor.class.getName();

}
