/*!! client */
package com.todomvc.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;
import com.todomvc.shared.service.ToDoServiceAsync;

/**
 * Loads model objects from the server at building time and after that keeps a client side
 * storage allowing to retrieve/add/remove model objects.
 *
 * @author Duilio Protti
 */
/*!
  Loads model objects from the server at building time and after that keeps a client side
  storage allowing to retrieve/add/remove model objects.
 */
public class ModelCache {

    private static final Logger logger = Logger.getLogger(ModelCache.class.getName());

    private ToDoList toDoList;

    @Inject
    public ModelCache(ToDoServiceAsync toDoService) {
        checkNotNull(toDoService);
        toDoService.getSingletonList(new AsyncCallback<ToDoList>() {

            @Override
            public void onSuccess(ToDoList list) {
                toDoList = list;
            }

            @Override
            public void onFailure(Throwable t) {
                String errorMessage = "Failed to obtain to-do list: " + t.getLocalizedMessage();
                logger.severe(errorMessage);
                throw new RuntimeException(errorMessage);

                // TODO should retry?

            }
            
        });
    }

    public void addToDo(ToDo toDo) {
        checkNotNull(toDo);
        checkState(toDoList != null);
        toDoList.add(toDo);
    }

    /*! Removes a to-do. Silently ignores unknown ids. */
    public void removeToDo(String id) {
        checkNotNull(id);
        checkState(toDoList != null);
        toDoList.remove(id);
    }

    @Nullable
    public ToDo getToDo(String id) {
        checkState(toDoList != null);
        return toDoList.getToDo(id);
    }

    @Nullable
    public ToDoList getToDoList() {
        checkState(toDoList != null);
        return toDoList;
    }

}
