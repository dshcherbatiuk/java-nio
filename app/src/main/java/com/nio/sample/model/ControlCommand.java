package com.nio.sample.model;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

public enum ControlCommand {

  STOP(0, "stop-read"), START(SelectionKey.OP_READ, "start-read");

  private final int operationCode;
  private final String name;

  ControlCommand(int operationCode, String name) {
    this.operationCode = operationCode;
    this.name = name;
  }

  public static ControlCommand getCommandByName(String name) {
    String searchCommand = name.trim();
    return Arrays.stream(values())
        .filter(command -> command.getName().equalsIgnoreCase(searchCommand))
        .findFirst().orElse(null);
  }

  public static String getAvailableCommands() {
    return String.format("Supported commands:%n%s%n%s%n",
        Arrays.stream(values()).map(ControlCommand::getName).toArray());
  }

  public int getOperationCode() {
    return operationCode;
  }

  public String getName() {
    return name;
  }
}
