package com.nio.tcp.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TcpPort {
  PORT_READ(4444),
  PORT_WRITE(5555);

  private final int address;

  TcpPort(int address) {
    this.address = address;
  }

  public int getAddress() {
    return address;
  }

  public static int[] getAdresses() {
    return Arrays.stream(TcpPort.values())
      .map(TcpPort::getAddress)
      .mapToInt(x -> x).toArray();
  }

  public static Optional<TcpPort> getPort(int port) {
    for (TcpPort value : TcpPort.values()) {
      if (port == value.address) {
        return Optional.of(value);
      }
    }
    return Optional.empty();
  }
}
