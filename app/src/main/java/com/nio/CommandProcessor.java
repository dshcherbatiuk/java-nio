package com.nio;

import java.nio.channels.SocketChannel;

//introduced interface data processing logic so that processing logic can be easily replaced (Liskov substitution principle)
//also may cover interface segregation principle
public interface CommandProcessor {
    void processCommand(SocketChannel client, String command, CommandHandler handler);
}

interface CommandHandler {
    void registerEvent(final int op);
}
