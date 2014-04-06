/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.todomvc.shared.command.BaseCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * {@link com.todomvc.shared.command.Command} that either adds or
 * else removes a task to/from a task list.
 */
/*!
  Creates a [Command](${basePath}/java/com/todomvc/shared/command/Command.java.html) that either
  adds or else removes a [ToDo](${basePath}/java/com/todomvc/shared/model/ToDo.java.html) to/from
  a given [ToDoList](${basePath}/java/com/todomvc/shared/model/ToDoList.java.html).
  
  Data carried:

  - **id** of target task list
  - **ToDo** to be added/removed
  
  The list is not transferred as part of the command, but only its id.
  On both server and client side, command executors will
  take care of loading the appropriate object and pass it to `Command.execute()`.

  This is a key concept of the Command Pattern: commands carry minimal information;
  only the **id** of the object that have changed, and the **delta**.
 */
public class AddOrRemoveToDoCommand extends BaseCommand<ToDoList> {

    private ToDo toDo;
    private boolean isAddition;

    protected AddOrRemoveToDoCommand() {
    }

    public AddOrRemoveToDoCommand(String toDoListId, ToDo toDo, boolean isAddition) {
        super(checkNotNull(toDoListId), ToDoListCommandExecutor.TYPE);
        this.toDo = checkNotNull(toDo);
        this.isAddition = isAddition;
    }

    /**
     * Returns true if it is and addition, false if it is a removal.
     */
    /*! Returns `true` if it's and addition, `false` if it's a removal. */
    public boolean isAddition() {
        return isAddition;
    }

    @Nullable
    public ToDo getAddedToDo() {
        if (isAddition) {
            return toDo;
        }
        return null;
    }

    @Nullable
    public ToDo getRemovedToDo() {
        if (!isAddition) {
            return toDo;
        }
        return null;
    }

    /*!
      Non-eager for additions, eager for removals.
      When adding a new task the client waits for the server response in order
      to have a proper `id` setup for the new task. 
     */
    @Override
    public boolean isEager() {
        return !isAddition;
    }

    @Override
    public void execute(ToDoList toDos) {
        checkNotNull(toDos);
        if (isAddition) {
            toDos.add(toDo);
        } else {
            toDos.remove(toDo);
        }
    }

    @Override
    public boolean canExecuteOn(ToDoList toDos) {
        checkNotNull(toDos);
        if (isAddition) {
            return toDo.getId() != null;
        }
        return toDos.contains(toDo);
    }

}
