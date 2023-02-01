package com.nio.sample;

import com.nio.sample.config.ConnectionConfig;
import com.nio.sample.handler.MessageHandler;
import com.nio.sample.handler.RegistrationUserHandler;
import com.nio.sample.repository.ClientStorage;
import java.nio.channels.SelectionKey;

public class App {

  public static void main(final String... args) {
    ConnectionConfig connectionConfig = ConnectionConfig.getInstance();
    TcpConnectionProvider connectionProvider = TcpConnectionProvider.getInstance(connectionConfig);
    ClientStorage clientStorage = ClientStorage.getInstance();
    connectionProvider.addEventHandler(SelectionKey.OP_READ,
        new MessageHandler(clientStorage, connectionProvider, connectionConfig));
    connectionProvider.addEventHandler(SelectionKey.OP_ACCEPT,
        new RegistrationUserHandler(clientStorage, connectionProvider));
    connectionProvider.listen();
  }
}