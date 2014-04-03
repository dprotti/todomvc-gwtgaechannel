/*!! shared.model */
package com.todomvc.shared.model;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

import javax.annotation.Nullable;

/*! Represents a task. */
public class ToDo implements HasID, Serializable {

    private String id;
    private String title;
    private boolean completed;

    protected ToDo() {
    }

    public ToDo(@Nullable String id, String title, boolean completed) {
        this.id = id;
        this.title = checkNotNull(title);
        this.completed = completed;
    }

    /**
     * ID might be {@code null}, for example when the to-do item is still transient
     * and was not yet persisted on server-side.
     */
    /*!
      ID might be `null`, for example when the to-do item is still transient
      and was not yet persisted on server-side.
     */
    @Override
    @Nullable
    public String getId() {
        return id;
    }

    /**
     * Sets the ID for the to-do item.
     *
     * An ID can be set only when the item does not have an ID already.
     */
    public void setId(String id) {
        checkState(this.id == null,
                "an ID can be set only when the item does not have an ID already");
        this.id = checkNotNull(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = checkNotNull(title);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /*- Object's methods overrides. */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ToDo)) {
            return false;
        }
        ToDo that = (ToDo) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("title", title)
                .add("completed", completed).toString();
    }
}
