package com.nio.sample.handler;

import com.nio.sample.TcpConnectionProvider;
import com.nio.sample.config.ConnectionConfig;
import com.nio.sample.model.ControlCommand;
import com.nio.sample.repository.ClientStorage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler implements EventHandler {

  private static final int UNSUCCESSFUL_REED_CODE = -1;
  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

  private final ClientStorage clientStorage;
  private final TcpConnectionProvider connectionProvider;
  private final ConnectionConfig connectionConfig;

  public MessageHandler(ClientStorage clientStorage, TcpConnectionProvider connectionProvider,
      ConnectionConfig connectionConfig) {
    this.clientStorage = clientStorage;
    this.connectionProvider = connectionProvider;
    this.connectionConfig = connectionConfig;
  }

  @Override
  public void processEvent(SelectionKey selectionKey) {
    LOGGER.info("Handle READ event");
    try {
      final SocketChannel client = (SocketChannel) selectionKey.channel();
      ByteBuffer buffer = getPayload(client);

      if (connectionConfig.getSendingPort() == client.socket().getLocalPort()) {
        sendLowercaseMessage(buffer, client);
      }
      if (connectionConfig.getControlPort() == client.socket().getLocalPort()) {
        processControlCommand(buffer, client);
      }
    } catch (IOException e) {
      LOGGER.error("Fail to proceed READ event because of: {}", e.getMessage());
    }
  }

  private ByteBuffer getPayload(SocketChannel channel) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(65535);
    try {
      final int read = channel.read(buffer);
      if (read == UNSUCCESSFUL_REED_CODE) {
        throw new IOException(
            String.format("Can't read payload from channel. Connection with client %s was closed", channel));
      }
    } catch (IOException e) {
      connectionProvider.closeConnectionWithChannel(channel);
      throw e;
    }

    buffer.flip();
    return buffer;
  }

  private void sendLowercaseMessage(ByteBuffer buffer, SocketChannel client) throws IOException {
    for (int charIndex = 0; charIndex < buffer.limit(); charIndex++) {
      buffer.put(charIndex, (byte) Character.toUpperCase(buffer.get(charIndex)));
    }
    client.write(buffer);
  }

  private void processControlCommand(ByteBuffer buffer, SocketChannel client) throws IOException {
    final String message = DEFAULT_CHARSET.decode(buffer).toString();

    ControlCommand controlCommand = ControlCommand.getCommandByName(message);
    if (Objects.isNull(controlCommand)) {
      client.write(ByteBuffer.wrap(ControlCommand.getAvailableCommands().getBytes(DEFAULT_CHARSET)));
    } else {
      LOGGER.info("Handle {}", controlCommand.getName());
      clientStorage.getClientsByPort(connectionConfig.getSendingPort()).forEach(client1 ->
          connectionProvider.registerEvent(client1, controlCommand.getOperationCode()));
    }
  }
}