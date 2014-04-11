package com.todomvc.shared.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous counterpart of {@link CommandService}.
 */
public interface CommandServiceAsync
{

    void openChannel( java.lang.String objectId, java.lang.String clientId, AsyncCallback<java.lang.String> callback );
    void closeChannel( java.lang.String clientId, AsyncCallback<java.lang.Void> callback );

    void executeCommand( com.todomvc.shared.command.Command<?> command, java.lang.String clientId, AsyncCallback<com.todomvc.shared.command.Command<?>> callback );

    void dummyCommand( AsyncCallback<com.todomvc.shared.command.Command<?>> callback );

    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util { 
        private static CommandServiceAsync instance;

        public static final CommandServiceAsync getInstance() {
            if ( instance == null ) {
                instance = (CommandServiceAsync) GWT.create( CommandService.class );
            }
            return instance;
        }

        private Util() {
            // Utility class should not be instantiated
        }
    }
}
