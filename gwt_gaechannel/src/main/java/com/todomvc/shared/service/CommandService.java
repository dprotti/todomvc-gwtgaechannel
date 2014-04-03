/*!! shared.service */
package com.todomvc.shared.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.todomvc.shared.command.Command;

/**
 * Maintains a registry of clients that can edit objects by executing commands
 * and sends an receives {@link Command}'s to and from clients.
 */
/*!
  Maintains a registry of clients that can edit objects by executing commands and sends an receives
  [Command](${basePath}/java/com/todomvc/shared/command/Command.java.html)'s
  to and from clients. Check the
  [implementation](${basePath}/java/com/todomvc/server/service/CommandServiceImpl.java.html)
 */
@RemoteServiceRelativePath("cmd")
public interface CommandService extends RemoteService {

	/**
	 * Opens a browser channel and subscribes the client for receiving object updates over the channel.
	 *
	 * @return the channel token.
	 * @throws IllegalArgumentException if the client attempts to open a channel for an object for
	 *             which it has a channel already open.
	 */
    /*!
      Opens a browser channel and subscribes the client for receiving object updates over the channel.
      It returns the channel token. Throws `IllegalArgumentException` if the client attempts to open
      a channel for an object for which it has a channel already open.
     */
	String openChannel(String objectId, String clientId) throws IllegalArgumentException;

	/**
	 * Executes a command. The command is executed on the server and on all listening clients.
	 */
	/*! Executes a command. The command is executed on the server and on all listening clients. */
	Command<?> executeCommand(Command<?> command, String clientId);

	/**
	 * Includes Command in serialization policy. Do not call.
	 */
	/*!
	  Declared only to force [Command](${basePath}/java/com/todomvc/shared/command/Command.java.html)
	  to be included in the serialization policy. Do not call.
	 */
	Command<?> dummyCommand();
}
