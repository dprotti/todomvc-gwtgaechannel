/*!! server */
package com.todomvc.server;

import javax.inject.Singleton;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.todomvc.server.command.CommandSerializer;
import com.todomvc.server.command.ServerCommandExecutor;
import com.todomvc.server.command.todo.ServerToDoCommandExecutor;
import com.todomvc.server.command.todo.ServerToDoListCommandExecutor;
import com.todomvc.server.service.CommandServiceImpl;
import com.todomvc.server.service.ToDoServiceImpl;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.command.todo.ToDoCommandExecutor;
import com.todomvc.shared.command.todo.ToDoListCommandExecutor;
import com.todomvc.shared.service.CommandService;
import com.todomvc.shared.service.ToDoService;

/*!
  Module configuration for the server side of the module.
  See client [ToDoGinModule](${basePath}/java/com/todomvc/client/ToDoGinModule.java.html).
 */
public class ToDoServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandExecutor.class).to(ServerCommandExecutor.class).in(Singleton.class);
        bind(CommandSerialization.Serializer.class).to(CommandSerializer.class).in(Singleton.class);
        bind(CommandService.class).to(CommandServiceImpl.class).in(Singleton.class);
        bind(ToDoListCommandExecutor.class).to(ServerToDoListCommandExecutor.class).in(Singleton.class);
        bind(ToDoCommandExecutor.class).to(ServerToDoCommandExecutor.class).in(Singleton.class);
        bind(ToDoService.class).to(ToDoServiceImpl.class).in(Singleton.class);
    }

    @Provides
    public ChannelService channelService() {
        return ChannelServiceFactory.getChannelService();
    }

}
