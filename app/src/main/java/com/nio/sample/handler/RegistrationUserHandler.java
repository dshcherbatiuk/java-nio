package com.nio.sample.handler;

import com.nio.sample.TcpConnectionProvider;
import com.nio.sample.repository.ClientStorage;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.slf4j.LoggerFactory;

public class RegistrationUserHandler implements EventHandler {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RegistrationUserHandler.class);

  private final ClientStorage clientStorage;
  private final TcpConnectionProvider connectionProvider;

  public RegistrationUserHandler(ClientStorage clientStorage, TcpConnectionProvider connectionProvider) {
    this.clientStorage = clientStorage;
    this.connectionProvider = connectionProvider;
  }

  @Override
  public void processEvent(SelectionKey selectionKey) {
    LOGGER.info("Handle ACCEPT event");
    try {
      final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
      final SocketChannel client = server.accept();
      client.configureBlocking(false);

      LOGGER.info("New connection accepted: {}", client);
      clientStorage.addClient(server.socket().getLocalPort(), client);

      connectionProvider.registerEvent(client, SelectionKey.OP_READ);
    } catch (IOException e) {
      LOGGER.error("Fail to proceed ACCEPT event because of: {}", e.getMessage());
    }
  }
}
