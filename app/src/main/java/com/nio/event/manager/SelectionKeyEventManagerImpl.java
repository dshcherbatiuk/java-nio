package com.nio.event.manager;

import com.nio.event.handler.SelectionKeyEventHandler;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class SelectionKeyEventManagerImpl implements SelectionKeyEventManager {

    private static final String UNSUPPORTED_OPERATION_MESSAGE = "Handler for operation with code %s not present";

    private final Map<Integer, SelectionKeyEventHandler> eventHandlerMap;

    public SelectionKeyEventManagerImpl() {
        eventHandlerMap = new HashMap<>();
    }

    @Override
    public void registerHandler(final Integer operationCode, final SelectionKeyEventHandler eventHandler) {
        eventHandlerMap.put(operationCode, eventHandler);
    }

    @Override
    public void handle(final SelectionKey selectionKey) {
        final SelectionKeyEventHandler handler = eventHandlerMap.get(selectionKey.readyOps());
        if (handler == null) {
            throw new UnsupportedOperationException(String.format(UNSUPPORTED_OPERATION_MESSAGE, selectionKey.readyOps()));
        }
        handler.handle(selectionKey);
    }
}
