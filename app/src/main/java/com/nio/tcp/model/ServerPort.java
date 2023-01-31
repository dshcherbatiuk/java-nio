package com.nio.tcp.model;

import java.util.Arrays;

public enum ServerPort {

    CONTROL_CHANNEL_PORT(4444),
    DATA_CHANNEL_PORT(5555);

    private final Integer portNumber;

    ServerPort(Integer portNumber) {
        this.portNumber = portNumber;
    }

    public static ServerPort fromValue(int intPort) {
        return Arrays.stream(ServerPort.values())
                .filter(port -> port.getPortNumber().equals(intPort))
                .findFirst()
                .orElse(null);
    }

    public Integer getPortNumber() {
        return portNumber;
    }
}