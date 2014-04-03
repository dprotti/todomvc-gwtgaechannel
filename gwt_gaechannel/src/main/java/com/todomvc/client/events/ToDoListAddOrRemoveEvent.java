/*!! client.events */
package com.todomvc.client.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.event.shared.EventHandler;
import com.todomvc.shared.model.ToDo;
import com.todomvc.shared.model.ToDoList;

/**
 * Event published when a task is added to or else removed from a task list.
 *
 * @author Duilio Protti
 */
/*! Event published when a task is added to or else removed from a task list. */
public class ToDoListAddOrRemoveEvent
        extends AddOrRemoveEvent<ToDo, ToDoList, ToDoListAddOrRemoveEvent.Handler> {

    public static final Type<Handler> TYPE = new Type<Handler>();

    public static interface Handler extends EventHandler {

        void onEvent(ToDoListAddOrRemoveEvent event);

    }

    public ToDoListAddOrRemoveEvent(ToDoList toDos, ToDo addedOrRemovedTask, boolean isAddition) {
        super(checkNotNull(toDos), checkNotNull(addedOrRemovedTask), isAddition);
    }

    public ToDoList getToDoList() {
        return getCollection();
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onEvent(this);
    }

}
