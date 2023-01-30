package com.nio.context;

import com.nio.DataFlowServerPort;
import com.nio.event.handler.SelectionKeyAcceptEventHandler;
import com.nio.event.handler.SelectionKeyReadEventHandler;
import com.nio.event.manager.SelectionKeyEventManager;
import com.nio.registrator.PortRegistrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataFlowServer {

    private final PortRegistrator portRegistrator;
    private final SelectionKeyEventManager eventManager;
    private final SelectionKeyAcceptEventHandler selectionKeyAcceptEventHandler;
    private final SelectionKeyReadEventHandler selectionKeyReadEventHandler;
    private final Logger logger = LoggerFactory.getLogger(DataFlowServer.class);


    public DataFlowServer(final PortRegistrator portRegistrator,
                          final SelectionKeyEventManager eventManager,
                          final SelectionKeyAcceptEventHandler selectionKeyAcceptEventHandler,
                          final SelectionKeyReadEventHandler selectionKeyReadEventHandler) {
        this.portRegistrator = portRegistrator;
        this.eventManager = eventManager;
        this.selectionKeyAcceptEventHandler = selectionKeyAcceptEventHandler;
        this.selectionKeyReadEventHandler = selectionKeyReadEventHandler;
    }

    public void bootServer(final String hostName) {
        eventManager.registerHandler(SelectionKey.OP_ACCEPT, selectionKeyAcceptEventHandler);
        eventManager.registerHandler(SelectionKey.OP_READ, selectionKeyReadEventHandler);

        final List<Integer> availablePorts = Arrays.stream(DataFlowServerPort.values())
                .map(DataFlowServerPort::getPortNumber)
                .collect(Collectors.toList());

        availablePorts
                .forEach(port -> portRegistrator.registerPort(port, hostName));

        logger.info("Tcp Data Flow Example started at {}:{}", hostName, availablePorts);
    }
}
