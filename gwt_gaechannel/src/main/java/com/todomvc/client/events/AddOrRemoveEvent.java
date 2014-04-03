/*!! client.events */
package com.todomvc.client.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AddOrRemoveEvent<T, C extends Collection<? extends T>, H extends EventHandler>
		extends GwtEvent<H> {

    private final C collection;
    private final T element;
    private final boolean isAddition;

    public AddOrRemoveEvent(C collection, T addedOrRemovedElement, boolean isAddition) {
        this.collection = checkNotNull(collection);
        element = checkNotNull(addedOrRemovedElement);
        this.isAddition = isAddition;
    }

    protected C getCollection() {
        return collection;
    }

    /**
     * Whether the element was added or removed.
     * 
     * @return true if the element was added to the collection, or false if the element was
     *         removed.
     */
    /*!
      Returns true if the element was added to the collection, or false if the element was
      removed.
      */
    public boolean isAddition() {
        return isAddition;
    }

    @Nullable
    public T getAddedElement() {
        if (isAddition) {
            return element;
        }
        return null;
    }

    @Nullable
    public T getRemovedElement() {
        if (!isAddition) {
            return element;
        }
        return null;
    }

}
