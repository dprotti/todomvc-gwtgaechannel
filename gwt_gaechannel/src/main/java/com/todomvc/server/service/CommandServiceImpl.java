/*!! server.service */
package com.todomvc.server.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.common.collect.Sets;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.service.CommandService;

/**
 * The server side implementation of {@link CommandService}.
 *
 * @TODO detect when a channel gets closed and remove the client from the editors.  
 */
/*!
  Server side implementation of
  [CommandService](${basePath}/java/com/todomvc/shared/service/CommandService.java.html).
 */
@Singleton
@SuppressWarnings("rawtypes")
public class CommandServiceImpl extends RemoteServiceServlet implements CommandService {

    /*!
      Set of editors. Editors are global, we don't keep track of which object they
      are editing. 

      In a more complex app you may instead have a Map of clients editing a given object
      were each entry would be `(objectId, [clientId1, clientId2, ...])`.
     */
    private final Set<String> editors = Sets.newHashSet();

    private final CommandExecutor executor;
    private final CommandSerialization.Serializer serializer;
    private final ChannelService channelService;

    @Inject
    public CommandServiceImpl(CommandExecutor executor, CommandSerialization.Serializer serializer,
            ChannelService channelService) {
        this.executor = checkNotNull(executor);
        this.serializer = checkNotNull(serializer);
        this.channelService = checkNotNull(channelService);
    }

    /*!
      Current implementation simply disregards `objectId` parameter. The client opens a
      "global" channel: will receive commands for any object being edited on the application.
      TodoMVC app is simple enough so as to work well with a global channel.
     */
    @Override
    public String openChannel(String objectId, String clientId) throws IllegalArgumentException,
            ChannelFailureException {
        //checkNotNull(objectId);
        checkNotNull(clientId);
        if (editors.contains(clientId)) {
            throw new IllegalArgumentException("clientId " + clientId + " has a channel open already");
        }
        String token = channelService.createChannel(clientId);
        editors.add(clientId);

        return token;
    }

    /*!
      Executes the command through an executor, probably a
      [ServerCommandExecutor](${basePath}/java/com/todomvc/server/command/ServerCommandExecutor.java.html),
      and notifies editing clients.
     */
    @Override
    public Command<?> executeCommand(Command<?> command, String originClientId) {
        Command<?> updateCommand = executor.execute(command);
        String commandMessage = serializer.serializeCommand(updateCommand);
        checkState(!editors.isEmpty(), "There must be at least one editing client");
        for (String editor : editors) {
            if (!editor.equals(originClientId)) {
                channelService.sendMessage(new ChannelMessage(editor, commandMessage));
            }
        }
        return updateCommand;
    }

    @Override
    public Command<?> dummyCommand() {
        throw new UnsupportedOperationException("this method should not be called");
    }
}
