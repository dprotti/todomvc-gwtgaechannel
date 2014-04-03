package com.todomvc.shared.model;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

public class ToDoList extends IdentifiableCollection<ToDo> {

    protected ToDoList() { }

    public ToDoList(String id) {
        super(checkNotNull(id));
    }

    @Nullable
    public ToDo getToDo(String id) {
        return super.getChild(id);
    }

}
