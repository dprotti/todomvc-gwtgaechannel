/*!! client.events */
package com.todomvc.client.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.todomvc.shared.model.ToDoList;

/*!
  Event triggered when several **existing** tasks of a task list have changed, but
  the size of the list didn't.
 */
public class ToDoListUpdatedEvent extends GwtEvent<ToDoListUpdatedEvent.Handler> {

    public static final Type<Handler> TYPE = new Type<Handler>();

    public static interface Handler extends EventHandler {

        void onEvent(ToDoListUpdatedEvent event);

    }

    private final ToDoList list;

    public ToDoListUpdatedEvent(ToDoList list) {
        this.list = checkNotNull(list);
    }

    public ToDoList getToDoList() {
        return list;
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
