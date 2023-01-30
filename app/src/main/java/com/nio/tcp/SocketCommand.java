package com.nio.tcp;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface SocketCommand {

  void execute(SocketChannel client, Selector selector) throws ClosedChannelException;

}
