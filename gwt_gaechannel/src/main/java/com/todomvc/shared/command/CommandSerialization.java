/*!! shared.command */
package com.todomvc.shared.command;

public interface CommandSerialization {

    /*!
      Serializes a [Command](${basePath}/java/com/todomvc/shared/command/Command.java.html)
      object. Used for serializing on the server the commands sent to the client.

      You may check this
      [implementation](${basePath}/java/com/todomvc/server/command/CommandSerializer.java.html).
     */
    public static interface Serializer {

        /**
         * Receives a command and returns its serialized representation.
         */
        String serializeCommand(Command<?> command);

    }

    /*!
      De-serializes a `Command` object.
      Used for de-serializing on the client the commands received from the server through the browser
      channel.

      Check this
      [implementation](${basePath}/java/com/todomvc/client/command/CommandDeserializerImpl.java.html).
     */
    public interface CommandDeserializer {

        public Command<?> read(String serializedCommand) throws SerializationException;

    }

    public static class SerializationException extends Exception {
        public SerializationException(Exception cause) {
            super(cause);
        }
    }

}
