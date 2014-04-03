/*!! shared.command */
package com.todomvc.shared.command;

/**
 * An object that executes commands of a specific type.
 * It loads the target object of a command and passes it to {@link Command#execute}.
 *
 * @param <C> The type of command this executor executes.
 */
/*!
  An object of this class executes commands of a specific type.
  It loads the target object of a command and passes it
  to [Command](${basePath}/java/com/todomvc/shared/command/Command.java.html)'s execute().
  Type `C` is the type of command this executor executes.
 */
public interface CommandExecutor<C extends Command<?>> {

    /**
     * Executes {@code command} and returns either the command that was passed or else a new command
     * suitable to update other clients.
     *
     * An example of returning a new command is when a new object was created: the returned command
     * might contain the new object.
     */
    /*!
      Executes `command` and returns either the command that was passed or else a new command
      suitable to update other clients.
      An example of returning a new command is when a new object was created: the returned command
      might contain the new object.
     */
    Command<?> execute(C command);

}
