package com.nio.tcp.handler;

import com.nio.tcp.handler.helper.EventHelper;

import java.nio.channels.SelectionKey;

public interface EventHandler {
    void initHelper(Integer operationCode, EventHelper eventHelper);

    void process(SelectionKey selectionKey);
}