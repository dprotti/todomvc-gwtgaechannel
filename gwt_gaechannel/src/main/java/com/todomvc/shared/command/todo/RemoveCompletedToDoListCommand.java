/*!! shared.command.todo */
package com.todomvc.shared.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.todomvc.shared.command.BaseCommand;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Removes from a task list a set of completed tasks.
 *
 * @author Duilio Protti
 */
/*! Removes from a task list a set of completed tasks. */
public class RemoveCompletedToDoListCommand extends BaseCommand<ToDoList> {

    private Set<String> idsOfTasksToRemove;

    protected RemoveCompletedToDoListCommand() {
    }

    public RemoveCompletedToDoListCommand(ToDoList list) {
        super(checkNotNull(list).getId(), ToDoListCommandExecutor.TYPE);
        idsOfTasksToRemove = Sets.newHashSet();
        for (ToDo task : list) {
            if (task.isCompleted()) {
                idsOfTasksToRemove.add(task.getId());
            }
        }
    }

    @Override
    public boolean isEager() {
        return true;
    }

    /*!
      Removes from `toDos` any task whose id is in `idsOfTasksToRemove`
      **and** that currently has a Completed status (status may have changed
      due to local edit or remote concurrent edit).

      Any other tasks are left untouched.
     */
    @Override
    public void execute(ToDoList toDos) {
        Iterator<ToDo> it = toDos.iterator();
        while(it.hasNext()) {
            ToDo task = it.next();
            if (idsOfTasksToRemove.contains(task.getId())
                    && task.isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public boolean canExecuteOn(ToDoList list) {
        checkNotNull(list);
        return !list.isEmpty();
    }

}
