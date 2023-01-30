package com.nio;

public enum DataFlowServerPort {

    CONTROL_CHANNEL_PORT(4444),
    DATA_CHANNEL_PORT(5555);

    private final Integer portNumber;

    DataFlowServerPort(final Integer portNumber) {
        this.portNumber = portNumber;
    }

    public Integer getPortNumber() {
        return portNumber;
    }
}
