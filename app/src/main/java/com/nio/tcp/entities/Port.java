package com.nio.tcp.entities;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum Port {
   READ_PORT(4444) {
       @Override
       public void execute(TcpDataFlowConnector connector, SocketChannel client) throws IOException {
           connector.read(client);
       }
   },
    WRITE_PORT(5555) {
        @Override
        public void execute(TcpDataFlowConnector connector, SocketChannel client) throws IOException {
            connector.write(client);
        }
    };

   private final int port;

    Port(int port) {
        this.port = port;
    }

    private static final Map<Integer, Port> availablePorts = new HashMap<>();
    static {
        for (Port port : Port.values()) {
            availablePorts.put(port.port, port);
        }
    }

    public static Set<Integer> getAvailablePorts() {
        return availablePorts.keySet();

    }

    public static Optional<Port> getPort(int port) {
        return Optional.of(availablePorts.get(port));
    }

    public abstract void execute(TcpDataFlowConnector connector, SocketChannel client) throws IOException;

}
