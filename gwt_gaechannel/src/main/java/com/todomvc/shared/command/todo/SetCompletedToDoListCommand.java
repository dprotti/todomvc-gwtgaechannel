/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.todomvc.shared.command.BulkSetterCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Command that set the completion status of every task in a list of tasks.
 */
/*!
  [BulkSetterCommand](${basePath}/java/com/todomvc/shared/command/BulkSetterCommand.java.html)
  that set the completion status of every task in a list of tasks.

  Data carried:

  - **id** of target task list
  - **status** value to be set
 */
public class SetCompletedToDoListCommand extends BulkSetterCommand<Boolean, ToDo, ToDoList> {

    protected SetCompletedToDoListCommand() {
    }

    public SetCompletedToDoListCommand(ToDoList list, boolean completed) {
        super(checkNotNull(list).getId(), completed, ToDoListCommandExecutor.TYPE);
    }

    @Override
    public boolean isEager() {
        return true;
    }

    @Override
    protected void setValue(ToDo toDo, Boolean completed) {
        checkNotNull(toDo);
        toDo.setCompleted(completed);
    }

    public boolean getCompleted() {
        return super.getValue();
    }

}
