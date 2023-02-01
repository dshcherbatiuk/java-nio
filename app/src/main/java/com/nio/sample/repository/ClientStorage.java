package com.nio.sample.repository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.nio.channels.SocketChannel;
import java.util.Collection;

public class ClientStorage {

  private static ClientStorage instance;

  private final Multimap<Integer, SocketChannel> clients;

  private ClientStorage() {
    clients = ArrayListMultimap.create();
  }

  public static ClientStorage getInstance() {
    if (instance == null) {
      instance = new ClientStorage();
    }

    return instance;
  }

  public void addClient(Integer portNumber, SocketChannel client) {
    clients.put(portNumber, client);
  }

  public Collection<SocketChannel> getClientsByPort(Integer portNumber) {
    return clients.get(portNumber);
  }
}
