package com.nio.context;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.event.handler.SelectionKeyAcceptEventHandler;
import com.nio.event.handler.SelectionKeyReadEventHandler;
import com.nio.event.manager.SelectionKeyEventManager;
import com.nio.event.manager.SelectionKeyEventManagerImpl;
import com.nio.registrator.PortRegistrator;
import com.nio.registrator.ServerSocketPortRegistrator;
import com.nio.strategy.ChannelProcessingStrategy;
import com.nio.strategy.ControlChannelProcessingStrategy;
import com.nio.strategy.DataChannelProcessingStrategy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class DataFlowContext {

    private static Selector selector;
    private static ByteBuffer buffer;
    private static Multimap<Integer, SocketChannel> clients;
    private static ChannelProcessingStrategy controlChannelProcessingStrategy;
    private static ChannelProcessingStrategy dataChannelProcessingStrategy;
    private static PortRegistrator portRegistrator;
    private static SelectionKeyEventManager selectionKeyEventManager;
    private static SelectionKeyAcceptEventHandler selectionKeyAcceptEventHandler;
    private static SelectionKeyReadEventHandler selectionKeyReadEventHandler;
    private static DataFlowServer dataFlowServer;

    private DataFlowContext() {
    }

    public static Selector selector() throws IOException {
        if (selector == null) {
            selector = Selector.open();
        }
        return selector;
    }

    public static ByteBuffer buffer() {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(65535);
        }
        return buffer;
    }

    public static Multimap<Integer, SocketChannel> clients() {
        if (clients == null) {
            clients = ArrayListMultimap.create();
        }
        return clients;
    }

    public static ChannelProcessingStrategy controlChannelProcessingStrategy() throws IOException {
        if (controlChannelProcessingStrategy == null) {
            controlChannelProcessingStrategy = new ControlChannelProcessingStrategy(clients(), selector());
        }
        return controlChannelProcessingStrategy;
    }

    public static ChannelProcessingStrategy dataChannelProcessingStrategy() {
        if (dataChannelProcessingStrategy == null) {
            dataChannelProcessingStrategy = new DataChannelProcessingStrategy();
        }
        return dataChannelProcessingStrategy;
    }

    public static PortRegistrator portRegistrator() throws IOException {
        if (portRegistrator == null) {
            portRegistrator = new ServerSocketPortRegistrator(selector());
        }
        return portRegistrator;
    }

    public static SelectionKeyEventManager selectionKeyEventManager() {
        if (selectionKeyEventManager == null) {
            selectionKeyEventManager = new SelectionKeyEventManagerImpl();
        }
        return selectionKeyEventManager;
    }

    public static List<ChannelProcessingStrategy> channelProcessingStrategies() throws IOException {
        final List<ChannelProcessingStrategy> strategies = new ArrayList<>();
        strategies.add(controlChannelProcessingStrategy());
        strategies.add(dataChannelProcessingStrategy());
        return strategies;
    }

    public static SelectionKeyAcceptEventHandler selectionKeyAcceptEventHandler() throws IOException {
        if (selectionKeyAcceptEventHandler == null) {
            selectionKeyAcceptEventHandler = new SelectionKeyAcceptEventHandler(selector(), clients());
        }
        return selectionKeyAcceptEventHandler;
    }

    public static SelectionKeyReadEventHandler selectionKeyReadEventHandler() throws IOException {
        if (selectionKeyReadEventHandler == null) {
            selectionKeyReadEventHandler = new SelectionKeyReadEventHandler(channelProcessingStrategies(), selector(), buffer());
        }
        return selectionKeyReadEventHandler;
    }

    public static DataFlowServer dataFlowServer() throws IOException {
        if (dataFlowServer == null) {
            dataFlowServer = new DataFlowServer(portRegistrator(), selectionKeyEventManager(), selectionKeyAcceptEventHandler(), selectionKeyReadEventHandler());
        }
        return dataFlowServer;
    }
}
