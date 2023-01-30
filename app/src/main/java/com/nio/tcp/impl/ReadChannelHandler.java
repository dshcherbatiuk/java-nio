package com.nio.tcp.impl;

import com.nio.tcp.ChannelHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class ReadChannelHandler implements ChannelHandler {

  private static final Logger LOGGER = Logger.getLogger(ReadChannelHandler.class.getName());

  @Override
  public void handleChannelRead(ByteBuffer buffer, SelectionKey key,
      Selector selector) {
    for (int i = 0; i < buffer.limit(); i++) {
      buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
    }
    final SocketChannel client = (SocketChannel) key.channel();
    try {
      client.write(buffer);
      LOGGER.info("Write to  buffer");
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
