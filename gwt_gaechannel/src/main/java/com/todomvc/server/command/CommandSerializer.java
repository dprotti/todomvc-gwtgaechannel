/*!! server.command */
package com.todomvc.server.command;

import java.lang.reflect.Method;

import com.todomvc.server.GwtRpcSerializer;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.service.CommandService;

/**
 * Helper class to serialize {@link Command} objects in the GWT RPC format.
 * 
 * @see com.todomvc.client.command.CommandDeserializer
 */
/*!
  Helper class to serialize commands in the GWT RPC format.

  GWT RPC has built-in support for serialization.
  It's used for moving objects between client and server when they communicate
  through [RemoteService's](http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuidePlumbingDiagram).

  But we can extend such use beyond RemoteService's.
  In this TodoMVC we re-use GWT RPC serialization for serializing `Command`'s
  the server sends to the clients through
  the [Channel API](https://developers.google.com/appengine/docs/java/channel/).
  */
public class CommandSerializer implements CommandSerialization.Serializer {

    private final GwtRpcSerializer gwtRpcSerializer;
    private final Method dummyMethod;

    public CommandSerializer() throws Exception {
        gwtRpcSerializer = new GwtRpcSerializer();
        dummyMethod = getDummyMethod();
    }

    @Override
    public String serializeCommand(Command<?> command) {
        return gwtRpcSerializer.serialize(command, dummyMethod);
    }

    /*!
       Returns the service method that is declared solely to force GWT into
       serializing Command objects for us.
     */
    private static Method getDummyMethod() {
        try {
            return CommandService.class.getDeclaredMethod("dummyCommand");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find the dummy RPC method.");
        }
    }
}
