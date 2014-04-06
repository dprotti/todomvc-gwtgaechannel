/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.todomvc.shared.command.SetterCommand;
import com.todomvc.shared.model.ToDo;

/**
 * Command that set the status of a task.
 */
/*!
  [SetterCommand](${basePath}/java/com/todomvc/shared/command/SetterCommand.java.html) that
  set the completion status of a task.

  Data carried:

  - **id** of target task
  - **status** value to be set
 */
public class SetCompletedToDoCommand extends SetterCommand<Boolean, ToDo> {

    protected SetCompletedToDoCommand() {
    }

    public SetCompletedToDoCommand(ToDo toDo, boolean completed) {
        super(checkNotNull(toDo).getId(), toDo.isCompleted(), completed, ToDoCommandExecutor.TYPE);
    }

    @Override
    public boolean isEager() {
        return true;
    }

    @Override
    protected Boolean getCurrentValue(ToDo toDo) {
        checkNotNull(toDo);
        return toDo.isCompleted();
    }

    @Override
    protected void setValue(ToDo toDo, Boolean completed) {
        checkNotNull(toDo);
        toDo.setCompleted(completed);
    }

}
