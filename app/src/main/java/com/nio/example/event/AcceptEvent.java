package com.nio.example.event;

import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEvent implements EventHandler {

  private final Multimap<Integer, SocketChannel> clients;
  private final Selector selector;

  public AcceptEvent(Multimap<Integer, SocketChannel> clients, Selector selector) {
    this.clients = clients;
    this.selector = selector;
  }

  @Override
  public boolean select(SelectionKey selectionKey) {
    return selectionKey.isAcceptable();
  }

  @Override
  public void handle(SelectionKey selectionKey) throws IOException {
    System.out.println("Handle READ event");
    final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
    final SocketChannel client = server.accept();
    client.configureBlocking(false);
    System.out.printf("New connection accepted: %s%n", client);

    clients.put(server.socket().getLocalPort(), client);

    client.register(selector, SelectionKey.OP_READ);
  }
}
