package com.nio.tcp.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.tcp.enums.TcpPort;
import com.nio.tcp.interfaces.TcpSocketsResolver;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpSocketsResolverImpl implements TcpSocketsResolver {

  private static final Multimap<Integer, SocketChannel> clients = ArrayListMultimap.create();
  private final Selector selector;

  public TcpSocketsResolverImpl(Selector selector) {
    this.selector = selector;
  }

  public void register(TcpPort[] tcpPorts, int op) {
    for (TcpPort tcpPort : tcpPorts) {
      clients.get(tcpPort.getAddress()).forEach(c -> {
        try {
          c.register(selector, op);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }

  public void acceptNewClient(ServerSocketChannel server) {
    try {
      final SocketChannel client = server.accept();
      client.configureBlocking(false);
      System.out.printf("New connection accepted: %s%n", client);
      clients.put(server.socket().getLocalPort(), client);
      client.register(selector, SelectionKey.OP_READ);
    }
    catch (IOException e) {
      System.out.println("IOException in acceptNewClient occured:\n");
      e.printStackTrace();
    }
  }

  public void bindSocketsToPorts(String hostname) {
    for (TcpPort port : TcpPort.values()) {
      try (final ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
        serverSocket.bind(new InetSocketAddress(hostname, port.getAddress()));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
      }
      catch (IOException e) {
        System.out.println("IOException in bindSocketsToPorts occured:\n");
        e.printStackTrace();
      }
    }
  }
}