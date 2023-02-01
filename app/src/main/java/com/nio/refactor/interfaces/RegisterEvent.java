package com.nio.refactor.interfaces;

import com.google.common.collect.Multimap;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public interface RegisterEvent {
    void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) throws ClosedChannelException;
}
