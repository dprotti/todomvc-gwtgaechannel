/*!! shared.service */
package com.todomvc.shared.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Service for tasks and task list handling.
 */
/*! Service for tasks and task list handling. */
@RemoteServiceRelativePath("todos")
public interface ToDoService extends RemoteService {

    /*!
      For TodoMVC we have just one single task list so there is no need to identify
      it neither to have a method to create lists since the service will take care
      of creating the singleton.

      For other apps we might want to support multiple lists, through methods of the
      kind of `List<ToDoList> getLists()`, `ToDoList getList(String id)` and
      `ToDoList newList()`.
     */
    ToDoList getSingletonList();
    void addToDo(String toDoListId, ToDo toDo);

    ToDo newToDo(String title, boolean completed);
    ToDo getToDo(String id);

}
