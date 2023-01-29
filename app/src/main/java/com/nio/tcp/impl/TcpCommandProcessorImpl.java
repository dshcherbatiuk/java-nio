package com.nio.tcp.impl;

import static com.nio.tcp.enums.TcpPort.PORT_WRITE;
import static java.nio.channels.SelectionKey.OP_READ;


import com.nio.tcp.enums.TcpCommand;
import com.nio.tcp.enums.TcpPort;
import com.nio.tcp.exceptions.TcpCommandProcessorException;
import com.nio.tcp.interfaces.TcpCommandProcessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TcpCommandProcessorImpl implements TcpCommandProcessor {

  private final TcpSocketsResolverImpl resolver;
  private final Map<TcpCommand, TcpCommandHolder> commandMap = new HashMap<>();

  public TcpCommandProcessorImpl(TcpSocketsResolverImpl resolver) {
    this.resolver = resolver;
    updateCommandMap();
  }

  private void updateCommandMap() {
    commandMap.put(TcpCommand.STOP_READ, new TcpCommandHolder("Handle stop-read", new TcpPort[] {PORT_WRITE}, 0));
    commandMap.put(TcpCommand.START_READ, new TcpCommandHolder("Handle start-read", new TcpPort[] {PORT_WRITE}, OP_READ));
  }

  public void process(String command) throws TcpCommandProcessorException {
    TcpCommand tcpCommand = TcpCommand.getTcpCommand(command);

    if (commandMap.containsKey(tcpCommand)) {
      resolve(commandMap.get(tcpCommand));
    }
    else {
      throw new TcpCommandProcessorException();
    }
  }

  private void resolve(TcpCommandHolder data) {
    System.out.println(data.message);
    resolver.register(data.conditions, data.operationCode);
  }

  public String getSupportedCommands() {
    return "Supported commands" + Arrays.stream(TcpCommand.values())
      .map(TcpCommand::getCommand)
      .collect(Collectors.joining(" "))
      .trim();
  }

  private static class TcpCommandHolder {
    final String message;
    final TcpPort[] conditions;
    final int operationCode;

    public TcpCommandHolder(String message, TcpPort[] conditions, int operationCode) {
      this.message = message;
      this.conditions = conditions;
      this.operationCode = operationCode;
    }
  }
}
