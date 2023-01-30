package com.nio.tcp.impl;

import com.nio.tcp.SocketCommand;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class StopCommand implements SocketCommand {

  private static final Logger LOGGER = Logger.getLogger(StopCommand.class.getName());

  private StopCommand() {
  }

  private static class SingletonHelper {

    private static final StopCommand INSTANCE = new StopCommand();
  }

  public static StopCommand getInstance() {
    return SingletonHelper.INSTANCE;
  }

  @Override
  public void execute(SocketChannel client, Selector selector) throws ClosedChannelException {
    LOGGER.info("Handle stop-read");
    client.register(selector, 0);
  }
}
