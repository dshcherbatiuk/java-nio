package com.nio.tcp.entities;

import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Command {

    START_READ_COMMAND("start-read") {
        @Override
        public void registerEvent(Selector selector, Multimap<Integer, SocketChannel> clients) throws ClosedChannelException {
            registerEvent(selector, clients, SelectionKey.OP_READ);
        }
    },
    STOP_READ_COMMAND("stop-read") {
        @Override
        public void registerEvent(Selector selector, Multimap<Integer, SocketChannel> clients) throws ClosedChannelException {
            registerEvent(selector, clients, 0);
        }
    };

    private final String commandName;

    public String getCommandName() {
        return commandName;
    }

    Command(String commandName) {
        this.commandName = commandName;
    }

    public abstract void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients) throws ClosedChannelException;

    private static final Map<String, Command> availableCommands = new HashMap<>();
    static {
        for (Command command : Command.values()) {
            availableCommands.put(command.commandName, command);
        }
    }

    public static Optional<Command> getCommand(String commandName) {
        return Optional.of(availableCommands.get(commandName));
    }

    static void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) throws ClosedChannelException {
        for (SocketChannel socketChannel : clients.get(5555)) {
            socketChannel.register(selector, op);
        }
    }

    static void unsupportedCommandAction(final SocketChannel client) throws IOException {
        final byte[] unknownCommand = String.format("Supported commands:%n%s%n", availableCommands.keySet())
                .getBytes(StandardCharsets.UTF_8);
        client.write(ByteBuffer.wrap(unknownCommand));
    }

}
