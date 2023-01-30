package com.nio.tcp.impl;

import static com.nio.tcp.enums.ChannelCommand.START;
import static com.nio.tcp.enums.ChannelCommand.STOP;

import com.nio.tcp.ChannelHandler;
import com.nio.tcp.SocketCommand;
import com.nio.tcp.enums.ChannelCommand;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class CommandChannelHandler implements ChannelHandler {

  private static final Map<ChannelCommand, SocketCommand> commands = new EnumMap<>(ChannelCommand.class);

  public CommandChannelHandler() {
    commands.put(START, StartCommand.getInstance());
    commands.put(STOP, StopCommand.getInstance());
  }

  @Override
  public void handleChannelRead(ByteBuffer buffer, SelectionKey key, Selector selector) throws ClosedChannelException {
    SocketChannel client = (SocketChannel) key.channel();
    final String command = StandardCharsets.UTF_8.decode(buffer).toString();

    ChannelCommand channelCommand = ChannelCommand.fromValue(command.trim());

    SocketCommand socketCommand = commands.get(channelCommand);
    if (Objects.nonNull(socketCommand)) {
      socketCommand.execute(client, selector);
    } else {
      final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", STOP.getValue(), START.getValue())
          .getBytes(StandardCharsets.UTF_8);
      try {
        client.write(ByteBuffer.wrap(unknownCommand));
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }
}
