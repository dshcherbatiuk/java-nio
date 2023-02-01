package com.nio.example.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.example.event.AcceptEvent;
import com.nio.example.event.EventHandler;
import com.nio.example.event.ReadEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.nio.TcpDataFlowExample.HOSTNAME;
import static com.nio.TcpDataFlowExample.PORTS;

public class TcpService {

  private final Selector selector;
  private final List<EventHandler> events;

  public TcpService() throws IOException {
    selector = Selector.open();

    for (final int port : PORTS) {
      final ServerSocketChannel serverSocket = ServerSocketChannel.open();
      serverSocket.bind(new InetSocketAddress(HOSTNAME, port));
      serverSocket.configureBlocking(false);
      serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    final Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();

    events = new ArrayList<>();
    events.add(new AcceptEvent(clients, selector));
    events.add(new ReadEvent(clients, selector));
  }

  public void run() throws IOException {
    while (!Thread.currentThread().isInterrupted()) {
      System.out.printf("Wait new events..%n");
      selector.select();

      final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      iterator.forEachRemaining(selectionKey -> {
        processing(selectionKey);
        iterator.remove();
      });
    }
  }

  private void processing(SelectionKey selectionKey) {
    events.stream()
          .filter(eventHandler -> eventHandler.select(selectionKey))
          .forEach(eventHandler -> {
            try {
              eventHandler.handle(selectionKey);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
  }
}
