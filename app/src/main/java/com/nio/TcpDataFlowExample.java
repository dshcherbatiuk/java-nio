package com.nio;


import com.nio.tcp.exceptions.TcpCommandProcessorException;
import com.nio.tcp.impl.TcpCommandProcessorImpl;
import com.nio.tcp.TcpDataBuffer;
import com.nio.tcp.impl.TcpSocketsResolverImpl;
import com.nio.tcp.enums.TcpPort;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

public final class TcpDataFlowExample {

  public static final String HOSTNAME = "0.0.0.0";

  private static TcpDataFlowExample program;
  private Selector selector;
  private TcpCommandProcessorImpl commandProcessor;
  private TcpSocketsResolverImpl resolver;
  private TcpDataBuffer dataBuffer;

  private TcpDataFlowExample() {
    try {
      dataBuffer = new TcpDataBuffer();
      selector = Selector.open();
      resolver = new TcpSocketsResolverImpl(selector);
      commandProcessor = new TcpCommandProcessorImpl(resolver);
    }
    catch (IOException e) {
      System.out.println("IOException in TcpDataFlowExample constructor");
      e.printStackTrace();
    }
  }

  public static TcpDataFlowExample getInstance() {
    if (program == null) {
      program = new TcpDataFlowExample();
    }
    return program;
  }

  public static void main(final String... args) throws Exception {
    TcpDataFlowExample instance = TcpDataFlowExample.getInstance();
    instance.run();
  }

  private void run() throws Exception {
    System.out.printf("Tcp Data Flow Example started at %s:%s%n", HOSTNAME, Arrays.toString(TcpPort.getAdresses()));
    resolver.bindSocketsToPorts(HOSTNAME);
    while (!Thread.currentThread().isInterrupted()) {
      System.out.printf("Wait new events..%n");
      int openedKeys = selector.select();
      if (openedKeys == 0) {
        continue;
      }
      final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      iterator.forEachRemaining(selectionKey -> {
        try {
          if (selectionKey.isAcceptable()) {
            final ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
            System.out.println("Handle READ event");
            resolver.acceptNewClient(server);
          }
          if (selectionKey.isReadable()) {
            final SocketChannel client = (SocketChannel) selectionKey.channel();
            readAndWriteData(client);
          }
          iterator.remove();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }

  private void readAndWriteData(SocketChannel client) throws IOException {
    System.out.println("Handle READ event");
    boolean isNotActive = dataBuffer.read(client);
    if (isNotActive) {
      client.close();
      client.keyFor(selector).cancel();
      System.out.printf("The connection was closed: %s%n", client);
    }
    else {
      Optional<TcpPort> port = TcpPort.getPort(client.socket().getLocalPort());
      if (port.isPresent()) {
        dataBuffer.flip();
        switch (port.get()) {
          case PORT_READ:
            dataBuffer.write(client);
            break;
          case PORT_WRITE:
            final String command = dataBuffer.decodeCommand();
            try {
              commandProcessor.process(command);
            }
            catch (TcpCommandProcessorException e) {
              final byte[] unknownCommand = commandProcessor.getSupportedCommands()
                .getBytes(StandardCharsets.UTF_8);
              client.write(ByteBuffer.wrap(unknownCommand));
            }
            break;
        }
      }
      dataBuffer.clear();
    }
  }
}
