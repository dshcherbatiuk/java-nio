package com.nio.tcp.interfaces;

import com.nio.tcp.exceptions.TcpCommandProcessorException;

public interface TcpCommandProcessor {
  void process(String command) throws TcpCommandProcessorException;

  String getSupportedCommands();
}
