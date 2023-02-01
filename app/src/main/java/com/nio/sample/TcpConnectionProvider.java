package com.nio.sample;

import com.nio.sample.config.ConnectionConfig;
import com.nio.sample.handler.EventHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpConnectionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TcpConnectionProvider.class);

  private static TcpConnectionProvider instance;

  private Selector selector;
  private Map<Integer, EventHandler> eventHandlers;

  private TcpConnectionProvider(ConnectionConfig connectionConfig) {
    this.eventHandlers = new HashMap<>();
    openConnection();
    initialiseSocketListenerForPort(connectionConfig.getControlPort(), connectionConfig.getHostname(), selector);
    initialiseSocketListenerForPort(connectionConfig.getSendingPort(), connectionConfig.getHostname(), selector);
  }

  public static TcpConnectionProvider getInstance(ConnectionConfig connectionConfig) {
    if (instance == null) {
      instance = new TcpConnectionProvider(connectionConfig);
    }
    return instance;
  }

  public void listen() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        LOGGER.info("Wait new events..");
        selector.select();
        selector.selectedKeys().forEach(selectionKey -> {
          if (eventHandlers.containsKey(selectionKey.readyOps())) {
            eventHandlers.get(selectionKey.readyOps()).processEvent(selectionKey);
          } else {
            LOGGER.warn("Message with this operation code - {} can't be handled", selectionKey.readyOps());
          }
        });
        selector.selectedKeys().clear();
      } catch (IOException e) {
        LOGGER.error("Fails while fetching tcp events. With error - {}. Closing connection", e.getMessage());
        closeConnection();
        return;
      }
    }
  }

  public void addEventHandler(Integer operationCode, EventHandler eventHandler) {
    eventHandlers.put(operationCode, eventHandler);
  }

  private void openConnection() {
    try {
      selector = Selector.open();
    } catch (IOException e) {
      LOGGER.error("Fails when trying to establish connection. With error - {}. Shutting down...", e.getMessage());
      System.exit(1);
    }
  }

  public void registerEvent(SocketChannel channel, int op) {
    try {
      channel.register(selector, op);
    } catch (ClosedChannelException e) {
      LOGGER.error("Fails during registering channel. With error - {}. Closing connection", e.getMessage());
      closeConnection();
    }
  }

  public void closeConnectionWithChannel(SocketChannel channel) {
    try {
      channel.close();
      channel.keyFor(selector).cancel();
      LOGGER.info("The connection was closed: {}", channel);
    } catch (IOException ex) {
      LOGGER.error("Fails to close connection {}", channel);
    }
  }

  private void initialiseSocketListenerForPort(Integer portNumber, String hostname, Selector selector) {
    try {
      ServerSocketChannel serverSocket = ServerSocketChannel.open();
      serverSocket.bind(new InetSocketAddress(hostname, portNumber));
      serverSocket.configureBlocking(false);
      serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    } catch (IOException e) {
      LOGGER.error("Fails to start listening port - {}. With error - {}", portNumber, e.getMessage());
    }
  }

  private void closeConnection() {
    try {
      selector.close();
    } catch (IOException e) {
      LOGGER.error("Fails when trying to close connection. With error - {}. Shutting down...", e.getMessage());
      System.exit(1);
    }
  }
}