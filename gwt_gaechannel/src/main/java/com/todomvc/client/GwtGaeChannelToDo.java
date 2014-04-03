/*!! client */
package com.todomvc.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point class.
 */
public class GwtGaeChannelToDo implements EntryPoint {

    private final ToDoGinjector injector = GWT.create(ToDoGinjector.class);

    public void onModuleLoad() {
        ToDoView mainPanel = injector.getMainPanel();
        RootPanel.get("mainContainer").add(mainPanel);
    }
}
