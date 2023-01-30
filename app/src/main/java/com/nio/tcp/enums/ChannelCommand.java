package com.nio.tcp.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ChannelCommand {

  START("start-read"),
  STOP("stop-read");

  private static final Map<String, ChannelCommand> COMMAND_TYPES = Arrays.stream(ChannelCommand.values())
      .collect(Collectors.toMap(ChannelCommand::getValue, Function.identity()));

  private String value;

  ChannelCommand(String s) {
    this.value = s;
  }

  public String getValue() {
    return this.value;
  }

  public static ChannelCommand fromValue(String value) {
    return COMMAND_TYPES.get(value);
  }

}
