package com.nio.tcpdataflowexample.server;

import java.io.IOException;

public abstract class TcpServer implements Server {

    protected String HOSTNAME;
    protected Integer PORT;

    public TcpServer hostname(String hostname) {
        this.HOSTNAME = hostname;
        return this;
    }

    public TcpServer port(int port) {
        this.PORT = port;
        return this;
    }

    public TcpServer build() {
        if(PORT == null || HOSTNAME == null) {
            throw new IllegalArgumentException("Port and hostname should be set");
        }

        return this;
    }

    public abstract void handleRequest() throws IOException;
}
