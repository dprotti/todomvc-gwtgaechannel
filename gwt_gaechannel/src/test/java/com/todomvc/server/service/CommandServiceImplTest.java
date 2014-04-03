package com.todomvc.server.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.todomvc.shared.command.Command;
import com.todomvc.shared.command.CommandExecutor;
import com.todomvc.shared.command.CommandSerialization;

@SuppressWarnings("rawtypes")
public class CommandServiceImplTest {

    private static final String ALICE = "Alice";
    private static final String BOB = "Bob";
    private static final String TODO1 = "todo1";

    @Mock private CommandExecutor executor;
    @Mock private CommandSerialization.Serializer serializer;
    @Mock private ChannelService channelService;
    @Mock private Command command;
    @Mock private Command updateCommand;

    private CommandServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new CommandServiceImpl(executor, serializer, channelService);
    }

    @Test
    public void testOpenChannel() {
        service.openChannel(TODO1, ALICE);
        verify(channelService).createChannel(eq(ALICE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientCanOpenOnlyOneChannelPerObject() {
        service.openChannel(TODO1, ALICE);
        service.openChannel(TODO1, ALICE);
    }

    @Test
    public void testExecuteCommand() {
        final ArgumentCaptor<ChannelMessage> captor = ArgumentCaptor.forClass(ChannelMessage.class);
        final String SERIALIZED_CMD = "serializedCommand";

        when(executor.execute(command)).thenReturn(updateCommand);
        when(updateCommand.getTargetId()).thenReturn(TODO1);
        when(serializer.serializeCommand(updateCommand)).thenReturn(SERIALIZED_CMD);
        // two clients edit the same object TODO1
        service.openChannel(TODO1, ALICE);
        service.openChannel(TODO1, BOB);

        service.executeCommand(command, ALICE);

        verify(executor).execute(eq(command));
        verify(serializer).serializeCommand(eq(updateCommand));
        // should send command to Bob
        verify(channelService).sendMessage(captor.capture());
        ChannelMessage capturedMessage = captor.getValue();
        assertEquals(BOB, capturedMessage.getClientId());
        assertEquals(SERIALIZED_CMD, capturedMessage.getMessage());
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteCommandFailsIfObjectIsNotBeingEdited() {

        when(executor.execute(command)).thenReturn(updateCommand);
        when(updateCommand.getTargetId()).thenReturn(TODO1);

        // nobody opened a command channel to edit TODO1. Not even Alice.
        // the service should not accept edits on TODO1.

        service.executeCommand(command, ALICE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDummyCommandIsUnusable() {
        service.dummyCommand();
    }

}
