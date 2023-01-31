package com.nio.tcp.handler.helper;

import java.nio.channels.SelectionKey;

public interface EventHelper {
    void processEvent(SelectionKey selectionKey);
}