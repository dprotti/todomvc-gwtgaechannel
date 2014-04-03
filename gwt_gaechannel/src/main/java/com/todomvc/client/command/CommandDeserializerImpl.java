/*!! client */
package com.todomvc.client.command;

import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.command.CommandSerialization.CommandDeserializer;
import com.todomvc.shared.service.CommandServiceAsync;

/**
 * Implementation of {@link CommandDeserializer} that uses GWT RPC.
 */
/*!
  Implementation of [CommandDeserializer](${basePath}/java/com/todomvc/client/command/CommandDeserializer.java.html)
  that uses GWT RPC serialization mechanism.

  It's the client counterpart of the server-side
  [CommandSerializer](${basePath}/java/com/todomvc/server/command/CommandSerializer.java.html).
 */
public class CommandDeserializerImpl implements CommandSerialization.CommandDeserializer {

    private static Logger logger = Logger.getLogger(CommandDeserializerImpl.class.getName());

    private final SerializationStreamFactory serializationStreamFactory;

    @Inject
    public CommandDeserializerImpl(CommandServiceAsync commandService) {
      /*!
         This looks like it shouldn't work, but the asynchronous implementation of
         [CommandService](${basePath}/java/com/todomvc/shared/service/CommandService.java.html)
         that GWT generates implements
         [SerializationStreamFactory](http://www.gwtproject.org/javadoc/latest/com/google/gwt/user/client/rpc/SerializationStreamFactory.html).
         Tricky part.
       */
      serializationStreamFactory = (SerializationStreamFactory) commandService;
    }

    
    /**
     * De-serializes a {@link Command} object.
     * 
     * @param s the serialized form of the command
     * @return a new command
     * @throws CommandSerialization.SerializationException if the parameter cannot be deserialized
     */
    /*! De-serializes a Command object. */
    @Override
    public Command<?> read(String s) throws CommandSerialization.SerializationException {
      try {
        SerializationStreamReader reader = serializationStreamFactory.createStreamReader(s);
        return (Command<?>) reader.readObject();
      } catch (com.google.gwt.user.client.rpc.SerializationException e) {
        logger.severe("unable to deserialize command: " + s);
        throw new CommandSerialization.SerializationException(e);
      }
    }

}
