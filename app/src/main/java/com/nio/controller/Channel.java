package com.nio.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Channel {
    DATA_CHANNEL(5555),
    CONTROL_CHANNEL(4444);

    private int port;

    Channel(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static Channel getChannelByPort(int port) {
        for(Channel channel : Channel.values()) {
            if(channel.getPort() == port) return channel;
        }
        return null;
    }

    public static List<String> getPorts() {
        return Arrays.stream(Channel.values()).map(a -> String.valueOf(a.getPort())).collect(Collectors.toList());
    }
}
