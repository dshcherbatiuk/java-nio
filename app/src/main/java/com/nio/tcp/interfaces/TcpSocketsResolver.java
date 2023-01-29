package com.nio.tcp.interfaces;

import com.nio.tcp.enums.TcpPort;
import java.nio.channels.ServerSocketChannel;

public interface TcpSocketsResolver {
  void register(TcpPort[] tcpPorts, int op);

  void acceptNewClient(ServerSocketChannel server);
}
