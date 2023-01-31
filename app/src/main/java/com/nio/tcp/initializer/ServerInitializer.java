package com.nio.tcp.initializer;

import com.nio.tcp.handler.EventHandler;
import com.nio.tcp.handler.SelectionKeyEventHandler;
import com.nio.tcp.handler.helper.AcceptEventHelper;
import com.nio.tcp.handler.helper.EventHelper;
import com.nio.tcp.handler.helper.ReadEventHelper;
import com.nio.tcp.model.ServerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerInitializer.class);
    private static ServerInitializer serverInitializerInstance;
    private final PortInitializer portInitializer;
    private final EventHandler eventHandler;
    private final EventHelper acceptEventHelper;
    private final EventHelper readEventHelper;


    private ServerInitializer() {
        this.portInitializer = ServerSocketPortInitializer.getInstance();
        this.eventHandler = SelectionKeyEventHandler.getInstance();
        this.acceptEventHelper = AcceptEventHelper.getInstance();
        this.readEventHelper = ReadEventHelper.getInstance();
    }

    public static ServerInitializer getInstance() {
        if (serverInitializerInstance == null) {
            serverInitializerInstance = new ServerInitializer();
        }
        return serverInitializerInstance;
    }

    public void initServer(String hostName) {
        eventHandler.initHelper(SelectionKey.OP_ACCEPT, acceptEventHelper);
        eventHandler.initHelper(SelectionKey.OP_READ, readEventHelper);

        List<Integer> ports = Arrays.stream(ServerPort.values())
                .map(ServerPort::getPortNumber)
                .collect(Collectors.toList());

        ports.forEach(port -> portInitializer.initPort(port, hostName));

        LOGGER.info("Tcp Data Flow Example started at {}:{}", hostName, ports);
    }
}