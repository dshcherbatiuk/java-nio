package com.nio;

import java.io.IOException;
import java.net.InetAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TcpDataFlowExampleTest {

  private Client clientOne;
  private Client clientTwo;

  @BeforeEach
  public void setup() throws IOException {
    clientOne = new Client();
    clientTwo = new Client();
    clientOne.startConnection(InetAddress.getLocalHost().getHostAddress(), 5555);
    clientTwo.startConnection(InetAddress.getLocalHost().getHostAddress(), 4444);
  }

  @AfterEach
  public void tearDown() throws IOException {
    clientOne.stopConnection();
    clientTwo.stopConnection();
  }

  @Test
  void givenClient_whenTcpMessage_thenCorrect() throws IOException {
    clientOne.sendMessage("1");
    clientTwo.sendMessage("stop-read");
    clientTwo.sendMessage("start-read");
    clientOne.sendMessage("world");
    clientOne.sendMessage(".");
    clientOne.sendMessage("world");
    clientOne.sendMessage("world");


  }
}
