package com.nio.tcp.handler;

import com.nio.tcp.exception.OperationNotPresentException;
import com.nio.tcp.handler.helper.EventHelper;

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

public class SelectionKeyEventHandler implements EventHandler {

    private static final String OPERATION_NOT_PRESENT_MESSAGE = "Handler for operation with code %s not present";
    private static EventHandler selectionKeyEventHandlerInstance;
    private final Map<Integer, EventHelper> eventHelperMap;

    private SelectionKeyEventHandler() {
        eventHelperMap = new HashMap<>();
    }

    public static EventHandler getInstance() {
        if (selectionKeyEventHandlerInstance == null) {
            selectionKeyEventHandlerInstance = new SelectionKeyEventHandler();
        }
        return selectionKeyEventHandlerInstance;
    }

    @Override
    public void initHelper(Integer operationCode, EventHelper eventHelper) {
        eventHelperMap.put(operationCode, eventHelper);
    }

    @Override
    public void process(SelectionKey selectionKey) {
        EventHelper eventHelper = eventHelperMap.get(selectionKey.readyOps());
        if (eventHelper == null) {
            throw new OperationNotPresentException(String.format(OPERATION_NOT_PRESENT_MESSAGE, selectionKey.readyOps()));
        }
        eventHelper.processEvent(selectionKey);
    }
}