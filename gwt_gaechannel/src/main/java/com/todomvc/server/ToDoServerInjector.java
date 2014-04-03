/*!! server */
package com.todomvc.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.todomvc.server.service.CommandServiceImpl;
import com.todomvc.server.service.ToDoServiceImpl;
import com.todomvc.server.servlet.ChannelConnectionsHandler;

/**
 * On the server, the injector is created when the servlet context is loaded.
 *
 * @author Duilio Protti
 */
/*! On the server, the injector is created when the servlet context is loaded. */
public class ToDoServerInjector extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        super.configureServlets();
                        serve("/gwtgaechanneltodo/cmd").with(CommandServiceImpl.class);
                        serve("/gwtgaechanneltodo/todos").with(ToDoServiceImpl.class);
                        serve("/_ah/channel/connected/").with(ChannelConnectionsHandler.class);
                        serve("/_ah/channel/disconnected/").with(ChannelConnectionsHandler.class);
                    }
                },
                new ToDoServerModule());
    }

}
