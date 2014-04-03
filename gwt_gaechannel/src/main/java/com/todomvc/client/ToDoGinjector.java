package com.todomvc.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(ToDoGinModule.class)
public interface ToDoGinjector extends Ginjector {

    ToDoView getMainPanel();

}
