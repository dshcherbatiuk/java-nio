package com.nio.example.event;

import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReadEvent implements EventHandler {

  private final Multimap<Integer, SocketChannel> clients;
  private final Selector selector;
  private final ByteBuffer buffer;

  public ReadEvent(Multimap<Integer, SocketChannel> clients, Selector selector) {
    this.clients = clients;
    this.selector = selector;
    buffer = ByteBuffer.allocate(65535);
  }

  private static void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) {
    clients.get(5555).forEach(c -> {
      try {
        c.register(selector, op);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public boolean select(SelectionKey selectionKey) {
    return selectionKey.isReadable();
  }

  @Override
  public void handle(SelectionKey selectionKey) throws IOException {
    System.out.println("Handle READ event");
    final SocketChannel client = (SocketChannel) selectionKey.channel();
    final int read = client.read(buffer);

    if (read == -1) {
      client.close();
      client.keyFor(selector).cancel();
      System.out.printf("The connection was closed: %s%n", client);
      return;
    }

    buffer.flip();
    switch (client.socket().getLocalPort()) {
      case 5555:
        for (int i = 0; i < buffer.limit(); i++) {
          buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        client.write(buffer);
        break;
      case 4444:
        final String command = StandardCharsets.UTF_8.decode(buffer).toString();
        final String stopCommand = "stop-read";
        if (stopCommand.equals(command.trim().toLowerCase())) {
          System.out.println("Handle stop-read");
          registerEvent(selector, clients, 0);
          break;
        }
        final String startCommand = "start-read";
        if (startCommand.equals(command.trim().toLowerCase())) {
          System.out.println("Handle start-read");
          registerEvent(selector, clients, SelectionKey.OP_READ);
          break;
        }

        final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", stopCommand, startCommand)
                                            .getBytes(StandardCharsets.UTF_8);
        client.write(ByteBuffer.wrap(unknownCommand));
        break;
    }

    buffer.clear();
  }
}
