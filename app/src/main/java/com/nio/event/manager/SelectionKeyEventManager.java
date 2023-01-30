package com.nio.event.manager;

import com.nio.event.handler.SelectionKeyEventHandler;

import java.nio.channels.SelectionKey;

public interface SelectionKeyEventManager {
    void registerHandler(Integer operationCode, SelectionKeyEventHandler eventHandler);
    void handle(SelectionKey selectionKey);
}
