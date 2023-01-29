package com.nio.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TcpDataBuffer {
  private final ByteBuffer buffer;

  public TcpDataBuffer() {
    buffer = ByteBuffer.allocate(65535);
  }

  public boolean read(SocketChannel client) throws IOException {
    return client.read(buffer) == -1;
  }

  public void flip() {
    buffer.flip();
  }

  public void clear() {
    buffer.clear();
  }

  public void write(SocketChannel client) throws IOException {
    for (int i = 0; i < buffer.limit(); i++) {
      buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
    }
    client.write(buffer);
  }

  public String decodeCommand() {
    return StandardCharsets.UTF_8.decode(buffer).toString().trim().toLowerCase();
  }
}
