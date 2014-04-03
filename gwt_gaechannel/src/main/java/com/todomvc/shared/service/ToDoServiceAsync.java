package com.todomvc.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Asynchronous counterpart of {@link ToDoService}.
 */
public interface ToDoServiceAsync
{

    void getSingletonList(AsyncCallback<ToDoList> callback);
    void addToDo(String toDoListId, ToDo toDo, AsyncCallback<Void> callback);

    void newToDo(String title, boolean completed, AsyncCallback<ToDo> callback);
    void getToDo(String id, AsyncCallback<ToDo> callback);

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static ToDoServiceAsync instance;

        public static final ToDoServiceAsync getInstance() {
            if ( instance == null ) {
                instance = (ToDoServiceAsync) GWT.create( ToDoService.class );
            }
            return instance;
        }

        private Util() {
            // Utility class should not be instantiated
        }
    }
}
