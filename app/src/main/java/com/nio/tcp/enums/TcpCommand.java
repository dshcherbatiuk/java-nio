package com.nio.tcp.enums;

public enum TcpCommand {
  STOP_READ("stop-read"),
  START_READ("start-read"),
  UNKNOWN("");
  private final String command;

  TcpCommand(String command) {
    this.command = command;
  }

  public static TcpCommand getTcpCommand(String command) {
    for (TcpCommand value : TcpCommand.values()) {
      if (value.command.equals(command)) {
        return value;
      }
    }
    return UNKNOWN;
  }

  public String getCommand() {
    return command;
  }
}
