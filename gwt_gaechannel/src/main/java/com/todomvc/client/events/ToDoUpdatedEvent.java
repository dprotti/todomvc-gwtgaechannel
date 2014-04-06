/*!! client.events */
package com.todomvc.client.events;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.todomvc.shared.model.ToDo;

/**
 * Event published when a task have changed.
 *
 * @author Duilio Protti
 */
/*! Event published when a task have changed. */
public class ToDoUpdatedEvent extends GwtEvent<ToDoUpdatedEvent.Handler> {

    public static final Type<Handler> TYPE = new Type<Handler>();

    public static interface Handler extends EventHandler {

        void onEvent(ToDoUpdatedEvent event);
    }

    private final ToDo toDo;

    public ToDoUpdatedEvent(ToDo toDo) {
        this.toDo = checkNotNull(toDo);
    }

    public ToDo getToDo() {
        return toDo;
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
