/*!! client */
package com.todomvc.client;

import javax.inject.Provider;

import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactoryImpl;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceHistoryHandler.DefaultHistorian;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.inject.Singleton;
import com.todomvc.client.command.CommandDeserializerImpl;
import com.todomvc.client.command.todo.ClientToDoCommandExecutor;
import com.todomvc.client.command.todo.ClientToDoListCommandExecutor;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;
import com.todomvc.shared.service.CommandServiceAsync;
import com.todomvc.shared.service.ToDoServiceAsync;

/*!
  Module configuration for the client side of the module.
  See [ToDoServerModule](${basePath}/java/com/todomvc/server/ToDoServerModule.java.html).
 */
public class ToDoGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ChannelFactory.class).to(ChannelFactoryImpl.class).in(Singleton.class);
        bind(CommandSerialization.CommandDeserializer.class).to(CommandDeserializerImpl.class).in(Singleton.class);
        bind(CommandServiceAsync.class).toProvider(CommandServiceProvider.class);
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(Historian.class).to(DefaultHistorian.class).in(Singleton.class);
        bind(ModelCache.class).in(Singleton.class);
        bind(ToDoCommandExecutor.class).to(ClientToDoCommandExecutor.class).in(Singleton.class);
        bind(ToDoListCommandExecutor.class).to(ClientToDoListCommandExecutor.class).in(Singleton.class);
        bind(ToDoServiceAsync.class).toProvider(ToDoServiceProvider.class);
        bind(ToDoPresenter.class).in(Singleton.class);
        bind(ToDoView.class).in(Singleton.class);
    }

    /*!- Providers */
    static class ToDoServiceProvider implements Provider<ToDoServiceAsync> {
        @Override
        public ToDoServiceAsync get() {
            return ToDoServiceAsync.Util.getInstance();
        }
    }

    static class CommandServiceProvider implements Provider<CommandServiceAsync> {
        @Override
        public CommandServiceAsync get() {
            return CommandServiceAsync.Util.getInstance();
        }
    }

}
