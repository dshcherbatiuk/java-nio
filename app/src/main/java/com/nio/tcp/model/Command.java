package com.nio.tcp.model;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

public enum Command {

    STOP_READ("stop-read", 0),
    START_READ("start-read", SelectionKey.OP_READ);

    private final String value;
    private final int selectionKey;

    Command(String value, int selectionKey) {
        this.value = value;
        this.selectionKey = selectionKey;
    }

    public static Command fromValue(String stringValue) {
        return Arrays.stream(Command.values())
                .filter(command -> command.getValue()
                        .equalsIgnoreCase(stringValue))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }

    public int getSelectionKey() {
        return selectionKey;
    }
}