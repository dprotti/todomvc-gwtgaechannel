/*!! server.command.todo */
package com.todomvc.server.command.todo;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.todo.AddOrRemoveToDoCommand;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoService;

/*!
  Server-side executor for commands acting on
  [ToDoList](${basePath}/java/com/todomvc/shared/model/ToDoList.java.html)'s.
*/
public class ServerToDoListCommandExecutor implements ToDoListCommandExecutor {

    private final ToDoService toDoService;
    private final ToDoList toDos;

    @Inject
    public ServerToDoListCommandExecutor(ToDoService toDoService) {
        this.toDoService = checkNotNull(toDoService);
        toDos = toDoService.getSingletonList();
    }

    @Override
    public Command<ToDoList> execute(Command<ToDoList> command) {
        checkNotNull(command);
        if (command instanceof AddOrRemoveToDoCommand) {
            AddOrRemoveToDoCommand addCommand = (AddOrRemoveToDoCommand) command;
            if (addCommand.isAddition()) {
                /*!
                 Create a new to-do and return it in an update command, so hence
                 clients can know the id of the newly created to-do.
                */
                ToDo toDoWithoutId = addCommand.getAddedToDo();

                ToDo toDoWithId = toDoService.newToDo(toDoWithoutId.getTitle(),
                        toDoWithoutId.isCompleted());
                toDoService.addToDo(toDos.getId(), toDoWithId);
                boolean isAddition = true;
                Command<ToDoList> updateCommand =
                        new AddOrRemoveToDoCommand(toDos.getId(), toDoWithId, isAddition);
                return updateCommand;
            }
        }
        if(command.canExecuteOn(toDos)) {
            command.execute(toDos);
        }
        return command;
    }

}
