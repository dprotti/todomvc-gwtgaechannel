/*!! client.command */
package com.todomvc.client.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelError;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.todomvc.client.util.UUID;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.command.CommandSerialization.CommandDeserializer;
import com.todomvc.shared.service.CommandServiceAsync;

/**
 * Coordinates command execution activity on the browser.
 *
 * <ul>
 *   <li>Registers client with server.</li>
 *   <li>Sends and receives {@link Command}'s to and from server.</li>
 * </ul>
 *
 * @author Duilio Protti
 */
/*!
  Coordinates command execution activity on the browser.

  - Registers client with server
  - Sends and receives Command's to and from server
  */
public class CommandController {

    private static Logger logger = Logger.getLogger(CommandController.class.getName());

    /*!
      Check this key piece: 
      [CommandService](${basePath}/java/com/todomvc/shared/service/CommandService.java.html).
     */
    private final CommandServiceAsync commandService;
    private final ChannelFactory channelFactory;
    private final ClientCommandExecutor executor;
    private final CommandDeserializer deserializer;
    private final java.util.Random random;
    private final String clientId;
    private SocketListener socketListener;

    @Inject
    public CommandController(CommandServiceAsync commandService, ChannelFactory channelFactory,
            ClientCommandExecutor executor, CommandDeserializer deserializer) {
        this.commandService = checkNotNull(commandService);
        this.channelFactory = checkNotNull(channelFactory);
        this.executor = checkNotNull(executor);
        this.deserializer = checkNotNull(deserializer);
        random = new java.util.Random();
        clientId = createUniqueClientId();
    } 

    /**
     * Registers client to edit a particular object.
     * 
     * @param objectId ID of the object the client wants to edit through commands.
     */
    /*!
      Registers client to edit a particular object.
      On success, client have established a Channel API channel with the server
      and can send and receive edit commands for the given object.
     */
    public void openCommandChannel(final String objectId) {
        commandService.openChannel(objectId, clientId, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.severe("cannot open command channel for object " + objectId);
            }

            @Override
            public void onSuccess(String token) {
                logger.info("opening channel for token: " + token);
                Channel channel = channelFactory.createChannel(token);
                socketListener = new CommandChannelListener();
                channel.open(socketListener);
            }

        });
    }

    @VisibleForTesting
    SocketListener getSocketListener() {
        return socketListener;
    }

    /*! Listener for commands received from the server through the Channel API. */
    private class CommandChannelListener implements SocketListener {

        @Override
        public void onOpen() {
            logger.info("channel open");
        }

        /*!
          Execute the commands received regardless of whether they
          are eager or not. Eagerness must be obeyed by originating client, not by
          listener clients.
         */
        @Override
        public void onMessage(String message) {
            try {
                Command<?> command = deserializer.read(message);
                logger.fine("received command: " + command);
                executor.execute(command);
            } catch (CommandSerialization.SerializationException e) {
                logger.severe("unable to de-serialize message: " + message);
            }
        }

        @Override
        public void onError(ChannelError error) {
            logger.severe("channel error: " + error.getCode() + " : " + error.getDescription());
        }

        @Override
        public void onClose() {
            logger.severe("channel closed: will not receive further commands from the server");
        }
    }

    /**
     * Executes command on the client eagerly, and then sends the command to the server
     * to execute in the future.
     */
    /*!
      Executes command on the client eagerly, and then sends the command to the server
      to execute in the future.
     */
    public void executeCommand(Command<?> command) {
        if (command.isEager()) {
            executeCommandOnClient(command);
        }
        sendCommandToServer(command);
    }

    private void executeCommandOnClient(Command<?> command) {
        executor.execute(command);
    }

    private void sendCommandToServer(final Command<?> command) {
        commandService.executeCommand(command, clientId, new AsyncCallback<Command<?>>() {

            @Override
            public void onFailure(Throwable caught) {
                logger.severe("error sending command to server: " + caught.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Command<?> updateCommand) {
                /*! If non-eager command, execute the update command sent back by the server. */
                if (!command.isEager()) {
                    executor.execute(updateCommand);
                }
            }
        });
    }

    /*!
      Creates a unique client ID suitable for AppEngine's 
      [ChannelService.createChannel](https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/channel/ChannelService)
      method.

      The client ID is expected to be unique among all of the clients connected to the server, and will always be
      fewer than 64 bytes when encoded to UTF-8.

      `UUID.uuid()` generates a RFC4122, version 4 ID. For example `92329D39-6F5C-4520-ABFC-AAB64544E172`.
     */
    private String createUniqueClientId() {
        return UUID.uuid() + ":" + random.nextInt(Integer.MAX_VALUE) + ":" + System.currentTimeMillis();
    }
}
