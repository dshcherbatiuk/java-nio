package com.nio.tcp.channel;

import com.nio.tcp.ChannelHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class NioServerSocketChannel {

  private final static Logger LOGGER = Logger.getLogger(NioServerSocketChannel.class.getName());

  private final int port;
  private final ServerSocketChannel channel;
  private final ChannelHandler handler;

  public NioServerSocketChannel(int port, ChannelHandler handler) throws IOException {
    this.channel = ServerSocketChannel.open();
    this.port = port;
    this.handler = handler;
  }

  public void bind() throws IOException {
    channel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
    channel.configureBlocking(false);
    LOGGER.info(String.format("Bound TCP socket at port: {}", port));
  }

  public ByteBuffer read(SelectionKey key, Selector selector) throws IOException {

    LOGGER.info("Handle READ event");
    final SocketChannel client = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(65535);
    final int read = client.read(buffer);

    if (read == -1) {
      client.close();
      client.keyFor(selector).cancel();
      LOGGER.info(String.format("The connection was closed: %s%n", client));
      throw new IOException("Socket closed");
    }

    buffer.flip();
    return buffer;
  }

  public ChannelHandler getHandler() {
    return handler;
  }

  public SelectableChannel getSelectableChannel() {
    return channel;
  }

}
