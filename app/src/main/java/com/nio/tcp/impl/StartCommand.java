package com.nio.tcp.impl;

import com.nio.tcp.SocketCommand;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class StartCommand implements SocketCommand {

  private static final Logger LOGGER = Logger.getLogger(StartCommand.class.getName());

  private StartCommand() {
  }

  private static class SingletonHelper {

    private static final StartCommand INSTANCE = new StartCommand();
  }

  public static StartCommand getInstance() {
    return SingletonHelper.INSTANCE;
  }

  @Override
  public void execute(SocketChannel client, Selector selector) throws ClosedChannelException {
    LOGGER.info("Handle start-read");
    client.register(selector, SelectionKey.OP_READ);
  }
}
