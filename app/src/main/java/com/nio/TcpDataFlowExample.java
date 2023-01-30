package com.nio;

import com.nio.tcp.ChannelHandler;
import com.nio.tcp.channel.NioServerSocketChannel;
import com.nio.tcp.impl.CommandChannelHandler;
import com.nio.tcp.impl.ReadChannelHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public final class TcpDataFlowExample {

  private static final Logger LOGGER = Logger.getLogger(TcpDataFlowExample.class.getName());

  private final Selector selector;

  public TcpDataFlowExample() throws IOException {
    this.selector = Selector.open();
  }

  public static void main(final String... args) throws Exception {
    TcpDataFlowExample tcpDataFlowExample = new TcpDataFlowExample();
    tcpDataFlowExample.start();
  }

  public void start() throws IOException {
    ReadChannelHandler readChannelHandler = new ReadChannelHandler();
    CommandChannelHandler commandChannelHandler = new CommandChannelHandler();
    this.registerChannel(tcpChannel(5555, readChannelHandler))
        .registerChannel(tcpChannel(4444, commandChannelHandler));
    try {
      LOGGER.info("Tcp Data Flow Example started ");
      processEvents();
    } catch (IOException e) {
      LOGGER.info("exception in event loop");
    }
  }

  private void processEvents() throws IOException {
    while (!Thread.interrupted()) {
      LOGGER.info("Wait new events..");
      selector.select();
      Set<SelectionKey> keys = selector.selectedKeys();
      Iterator<SelectionKey> iterator = keys.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        if (!key.isValid()) {
          iterator.remove();
          continue;
        }
        processKey(key);
      }
      keys.clear();
    }
  }

  private void processKey(SelectionKey key) throws IOException {
    if (key.isAcceptable()) {
      onChannelAcceptable(key);
    } else if (key.isReadable()) {
      onChannelReadable(key);
    }
  }

  private void onChannelAcceptable(SelectionKey key) throws IOException {
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    SocketChannel socketChannel = serverSocketChannel.accept();
    socketChannel.configureBlocking(false);
    LOGGER.info(String.format("New connection accepted: %s%n", socketChannel));
    SelectionKey readKey = socketChannel.register(selector, SelectionKey.OP_READ);
    readKey.attach(key.attachment());
  }

  private void onChannelReadable(SelectionKey key) {
    try {
      NioServerSocketChannel channel = (NioServerSocketChannel) key.attachment();
      ByteBuffer readObject = channel.read(key, selector);
      channel.getHandler().handleChannelRead(readObject, key, selector);
      readObject.clear();
    } catch (IOException e) {
      try {
        key.channel().close();
      } catch (IOException e1) {
        LOGGER.info("error closing channel");
      }
    }
  }

  public TcpDataFlowExample registerChannel(NioServerSocketChannel channel) throws IOException {
    SelectionKey key = channel.getSelectableChannel().register(selector, SelectionKey.OP_ACCEPT);
    key.attach(channel);
    return this;
  }


  private NioServerSocketChannel tcpChannel(int port, ChannelHandler handler) throws IOException {
    NioServerSocketChannel channel = new NioServerSocketChannel(port, handler);
    channel.bind();
    return channel;
  }

}
