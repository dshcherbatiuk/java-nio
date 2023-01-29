package com.nio.tcpdataflowexample.command;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Command {
    START_READ, STOP_READ, EXIT, UNKNOWN;

    public static Command getCommandFromString(String commandString) {
        final String command = commandString.trim().toUpperCase().replace("-", "_");
        return Arrays.stream(Command.values())
                .filter(aEnum -> aEnum.name().equals(command))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public static String getCommandsListAsString() {
        return Arrays.stream(Command.values())
                .filter(aEnum -> !aEnum.equals(UNKNOWN))
                .map(Command::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }
}
