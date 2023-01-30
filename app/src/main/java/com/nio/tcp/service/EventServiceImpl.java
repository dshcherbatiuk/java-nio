package com.nio.tcp.service;

import static com.nio.tcp.enums.Command.START_READ;
import static com.nio.tcp.enums.Command.STOP_READ;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.nio.tcp.enums.Command;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class EventServiceImpl implements EventService {

  private static final Logger LOG = Logger.getLogger(EventServiceImpl.class.getName());
  private static final int BUFFER_CAPACITY = 65535;

  private final Selector selector;
  private final ByteBuffer buffer;
  private final Multimap<Integer, SocketChannel> clients;

  public EventServiceImpl(Selector selector) {
    this.selector = selector;
    this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
    this.clients = ArrayListMultimap.create();
  }

  @Override
  public void acceptEvent(ServerSocketChannel channel) throws IOException {
    final SocketChannel client = channel.accept();
    client.configureBlocking(false);
    LOG.info(String.format("New connection accepted: %s", client));

    clients.put(channel.socket()
        .getLocalPort(), client);

    client.register(selector, SelectionKey.OP_READ);
  }

  @Override
  public void readEvent(SocketChannel channel, int writingPort, int readingPort) throws IOException {
    checkConnection(channel);
    buffer.flip();
    int port = channel.socket()
        .getLocalPort();
    if (port == writingPort) {
      writeToChannel(channel);
    } else if (port == readingPort) {
      handleCommand(writingPort, channel);
    } else {
      LOG.info(String.format("Unprocessed port: %s", port));
    }

    buffer.clear();
  }

  private void registerEvent(final Selector selector, final int op, int writingPort) {
    clients.get(writingPort)
        .forEach(c -> {
          try {
            c.register(selector, op);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void checkConnection(SocketChannel channel) throws IOException {
    if (channel.read(buffer) == -1) {
      channel.close();
      channel.keyFor(selector)
          .cancel();
      throw new IOException(String.format("The connection was closed: %s", channel));
    }
  }

  private void writeToChannel(SocketChannel channel) throws IOException {
    for (int i = 0; i < buffer.limit(); i++) {
      buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
    }
    channel.write(buffer);
  }

  private void handleCommand(int writingPort, SocketChannel channel) throws IOException {
    final Command command = Command.fromValue(StandardCharsets.UTF_8.decode(buffer)
        .toString());
    if (command != null) {
      LOG.info(String.format("Handle %s", command.getValue()));
      registerEvent(selector, command.getSelectionKey(), writingPort);
    } else {
      final byte[] unknownCommand = String
          .format("Supported commands: %s, %s", STOP_READ.getValue(), START_READ.getValue())
          .getBytes(StandardCharsets.UTF_8);
      channel.write(ByteBuffer.wrap(unknownCommand));
    }
  }
}
