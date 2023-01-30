package com.nio.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface ChannelHandler {

  void handleChannelRead(ByteBuffer readObject, SelectionKey key, Selector selector) throws ClosedChannelException;
}
