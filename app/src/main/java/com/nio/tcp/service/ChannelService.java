package com.nio.tcp.service;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public interface ChannelService {

  void acceptChannel(ServerSocketChannel channel) throws IOException;

  void readChannel(SocketChannel channel, int writingPort, int readingPort) throws IOException;
}
