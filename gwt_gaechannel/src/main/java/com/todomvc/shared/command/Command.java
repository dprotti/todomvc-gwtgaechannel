/*!! shared.command */
package com.todomvc.shared.command;

import java.io.Serializable;

import com.todomvc.shared.model.HasID;

/**
 * Command acting on an object of type T.
 */
/*! Command acting on an object of type T. */
public interface Command<T extends HasID> extends Serializable {

    /**
     * Eager commands are evaluated on the client before sending to the server.
     * When <strong>non</strong> eager, the one sent back from the server is executed instead.
     */
    /*!
      Eager commands are evaluated on the client before sending to the server.
      When **non** eager, the one sent back from the server is executed instead.
     */
    boolean isEager();

    /**
     * Execute the command on {@code target}.
     */
    /*! Execute the command on `target`. */
    void execute(T target);

    /**
     * Whether the command can proceed to execute on {@code target}.
     */
    /*! Whether the command can proceed to execute on `target`. */
    boolean canExecuteOn(T target);

    /**
     * Gets the type of command, used to determine which executor should execute the command.
     */
    /*! Gets the type of command, used to determine which executor should execute the command. */
    String getCommandType();

    /**
     * Gets the ID that uniquely identifies the object on which this command executes on.
     */
    /*!
      Gets the ID that uniquely identifies the object (which
      [HasID](${basePath}/java/com/todomvc/shared/model/HasID.java.html)) on
      which this command executes on.
     */
    String getTargetId();

}
