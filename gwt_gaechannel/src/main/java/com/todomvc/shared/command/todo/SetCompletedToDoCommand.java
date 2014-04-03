/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.todomvc.shared.command.SetterCommand;
import com.todomvc.shared.model.ToDo;

/*!
  [SetterCommand](${basePath}/java/com/todomvc/shared/command/SetterCommand.java.html) setting
  the status of a task. 
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
