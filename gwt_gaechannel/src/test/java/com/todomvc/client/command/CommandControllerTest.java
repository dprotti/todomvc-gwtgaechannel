package com.todomvc.client.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandSerialization;
import com.todomvc.shared.command.CommandSerialization.CommandDeserializer;
import com.todomvc.shared.service.CommandServiceAsync;

public class CommandControllerTest {

    @Mock private CommandServiceAsync commandService;
    @Mock private ChannelFactory channelFactory;
    @Mock private ClientCommandExecutor executor;
    @Mock private CommandDeserializer deserializer;
    @Mock private Channel channel;
    @Mock private Command command;

    private CommandController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new CommandController(commandService, channelFactory, executor, deserializer);
    }

    @Test
    public void testOpenCommandChannel() {
        String token = openCommandChannelSuccessfully();

        verify(channelFactory).createChannel(token);
        verify(channel).open(any(SocketListener.class));
    }

    @Test
    public void testCommandsReceivedFromServerAreExecuted() throws CommandSerialization.SerializationException {
        String serializedCommand = "someEncodedCommand";
        when(deserializer.read(serializedCommand)).thenReturn(command);

        openCommandChannelSuccessfully();
        SocketListener listener = controller.getSocketListener();

        listener.onMessage(serializedCommand);

        verify(executor).execute(command);
    }

    @Test
    public void testExecuteCommandExecutesEagerLocallyAndSendsCommandToServer() {
        when(command.isEager()).thenReturn(true);

        controller.executeCommand(command);

        verify(executor).execute(eq(command));
        verify(commandService).executeCommand(eq(command), anyString(), any(AsyncCallback.class));
    }

    @Test
    public void testExecuteCommandSendsNonEagerToServer() {
        when(command.isEager()).thenReturn(false);

        controller.executeCommand(command);

        verify(executor, never()).execute(any(Command.class));
        verify(commandService).executeCommand(eq(command), anyString(), any(AsyncCallback.class));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String openCommandChannelSuccessfully() {
        String objectId = "someObject";
        String token = "someToken";
        ArgumentCaptor<AsyncCallback> captor = ArgumentCaptor.forClass(AsyncCallback.class);

        controller.openCommandChannel(objectId);

        verify(commandService).openChannel(eq(objectId), anyString(), (AsyncCallback<String>) captor.capture());

        AsyncCallback<String> callback = captor.getValue();
        when(channelFactory.createChannel(token)).thenReturn(channel);

        callback.onSuccess(token);

        return token;
    }

}
