package com.nio.refactor.enums;

import com.google.common.collect.Multimap;
import com.nio.refactor.interfaces.RegisterEvent;
import sun.jvm.hotspot.asm.Register;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public enum Commands implements RegisterEvent {
    START_READ("start-read"){
        public void registerEvent(Selector selector, Multimap<Integer, SocketChannel> clients) throws ClosedChannelException {
            registerEvent(selector, clients, SelectionKey.OP_READ);
        }
    },
    STOP_READ("stop-read"){
        public void registerEvent(Selector selector, Multimap<Integer, SocketChannel> clients) throws ClosedChannelException {
            registerEvent(selector, clients, 0);
        }
    };

    private final String commandName;

    public String getCommandName(){
        return commandName;
    }

    Commands(String commandName){
        this.commandName = commandName;
        fillCommands();
    }
    private static HashMap<String, Commands> commandNames = new HashMap<>();
    private void fillCommands(){
        for (Commands command : Commands.values()){
            commandNames.put(command.commandName, command);
        }
    }

    public static Set<String> getCommandNames(){
        return commandNames.keySet();
    }
    public static Optional<Commands> getCommandOptional(String commandName){
        for (Commands command : Commands.values()){
            if (Objects.equals(commandName, command.commandName)){
                return Optional.of(command);
            }
        }
        return null;
    }

    private static void getUnknownCommand(final SocketChannel client) throws IOException {
        final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", getCommandNames())
                .getBytes(StandardCharsets.UTF_8);
        client.write(ByteBuffer.wrap(unknownCommand));
    }

    @Override
    public void registerEvent(Selector selector, Multimap<Integer, SocketChannel> clients, int op) throws ClosedChannelException {
        for (SocketChannel socketChannel : clients.get(5555)) {
            socketChannel.register(selector, op);
        }
    }
}
