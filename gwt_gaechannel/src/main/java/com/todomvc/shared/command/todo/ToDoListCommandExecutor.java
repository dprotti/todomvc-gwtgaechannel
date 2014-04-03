/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.model.ToDoList;

/*! Executor for commands acting on task lists. */
public interface ToDoListCommandExecutor extends CommandExecutor<Command<ToDoList>> {

    public static final String TYPE = ToDoListCommandExecutor.class.getName();

}
