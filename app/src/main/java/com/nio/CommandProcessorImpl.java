package com.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

//ensure single-responsibility
public class CommandProcessorImpl implements CommandProcessor {

    public static final String START_READ = "start-read";
    public static final String STOP_READ = "stop-read";

    public void processCommand(SocketChannel client, String command, CommandHandler handler) {
        System.out.println("Received command: " + command);
        if (STOP_READ.equalsIgnoreCase(command.trim())) {
            System.out.println("Handle stop-read");
            handler.registerEvent(0);
            return;
        }
        if (START_READ.equalsIgnoreCase(command.trim())) {
            System.out.println("Handle start-read");
            handler.registerEvent(SelectionKey.OP_READ);
            return;
        }

        final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", START_READ, STOP_READ)
                .getBytes(StandardCharsets.UTF_8);
        try {
            client.write(ByteBuffer.wrap(unknownCommand));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


