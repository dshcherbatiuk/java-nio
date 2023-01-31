package com.nio.tcp.singleton;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.nio.channels.SocketChannel;

public class SocketChannelsSingletone {
    private static Multimap<Integer, SocketChannel> socketChannelsInstance;

    private SocketChannelsSingletone() {
    }

    public static Multimap<Integer, SocketChannel> getInstance() {
        if (socketChannelsInstance == null) {
            socketChannelsInstance = ArrayListMultimap.create();
        }
        return socketChannelsInstance;
    }
}
