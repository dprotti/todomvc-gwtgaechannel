/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import com.todomvc.shared.command.SetterCommand;
import com.todomvc.shared.model.ToDo;

/*!
  [SetterCommand](${basePath}/java/com/todomvc/shared/command/SetterCommand.java.html) setting
  the title of a task. 
 */
public class SetTitleToDoCommand extends SetterCommand<String, ToDo> {

    protected SetTitleToDoCommand() {
    }

    public SetTitleToDoCommand(ToDo toDo, String title) {
        super(checkNotNull(toDo).getId(), toDo.getTitle(), checkNotNull(title), ToDoCommandExecutor.TYPE);
    }

    @Override
    public boolean isEager() {
        return true;
    }

    @Override
    protected String getCurrentValue(ToDo toDo) {
        checkNotNull(toDo);
        return toDo.getTitle();
    }

    @Override
    protected void setValue(ToDo toDo, String title) {
        checkNotNull(title);
        toDo.setTitle(title);
    }

}
