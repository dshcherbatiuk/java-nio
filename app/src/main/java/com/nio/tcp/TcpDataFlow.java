package com.nio.tcp;

import com.nio.tcp.service.EventService;
import com.nio.tcp.service.EventServiceImpl;
import com.nio.TcpDataFlowExample;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

public class TcpDataFlow {

  private static final Logger LOG = Logger.getLogger(TcpDataFlowExample.class.getName());
  private final String hostname;
  private final int writingPort;
  private final int readingPort;
  private final EventService eventService;
  private final Selector selector;

  public TcpDataFlow(String hostname, int writingPort, int readingPort) throws IOException {
    this.selector = Selector.open();
    this.eventService = new EventServiceImpl(selector);
    this.hostname = hostname;
    this.writingPort = writingPort;
    this.readingPort = readingPort;
  }

  public void start() throws IOException {
    LOG.info(String.format("Tcp Data Flow Example started at %s:[%s, %s]", hostname, writingPort, readingPort));

    registerPorts();

    while (!Thread.currentThread()
        .isInterrupted()) {
      LOG.info("Wait new events..");

      selector.select();

      processNewEvent();
    }
  }

  private void processNewEvent() {
    final Iterator<SelectionKey> iterator = selector.selectedKeys()
        .iterator();
    iterator.forEachRemaining(selectionKey -> {
      try {
        LOG.info("Handle READ event");
        if (selectionKey.isAcceptable()) {
          eventService.acceptEvent((ServerSocketChannel) selectionKey.channel());
        }

        if (selectionKey.isReadable()) {
          SocketChannel channel = (SocketChannel) selectionKey.channel();
          eventService.readEvent(channel, writingPort, readingPort);
        }
        iterator.remove();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private void registerPorts() throws IOException {
    registerPort(writingPort);
    registerPort(readingPort);
  }

  private void registerPort(int port) throws IOException {
    final ServerSocketChannel serverSocket = ServerSocketChannel.open();
    serverSocket.bind(new InetSocketAddress(hostname, port));
    serverSocket.configureBlocking(false);
    serverSocket.register(selector, SelectionKey.OP_ACCEPT);
  }
}
