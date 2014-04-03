/*!! server.service */
package com.todomvc.server.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import javax.inject.Singleton;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoService;

/**
 * Server side implementation of {@link ToDoService}.
 */
/*!
  Server side implementation
  of [ToDoService](${basePath}/java/com/todomvc/shared/service/ToDoService.java.html).
 */
@Singleton
public class ToDoServiceImpl extends RemoteServiceServlet implements ToDoService {

    private static int nextId = 1;

    /*! Server side task list lives in memory. No Datastore for now. */
    private final ToDoList uniqueList;

    public ToDoServiceImpl() {
        this.uniqueList = new ToDoList("todos");
    }

    @Override
    public ToDoList getSingletonList() {
        return uniqueList;
    }

    @Override
    public void addToDo(String toDoListId, ToDo toDo) {
        checkNotNull(toDoListId);
        checkNotNull(toDo);
        if (!uniqueList.getId().equals(toDoListId)) {
            throw new IllegalArgumentException("There is no list with id " + toDoListId);
        }
        uniqueList.add(toDo);
    }

    @Override
    public ToDo newToDo(String title, boolean completed) {
        checkNotNull(title);
        ToDo toDo = new ToDo(Integer.valueOf(nextId++).toString(), title, completed);
        return toDo;
    }

    @Override
    public ToDo getToDo(String id) {
        for (ToDo toDo : uniqueList) {
            if (toDo.getId().equals(id)) {
                return toDo;
            }
        }
        throw new IllegalArgumentException(format("No to-do found with id '%s'", id));
    }
}
