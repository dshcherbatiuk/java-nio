# java-nio

There are 3 examples using Java Nio library:

## 1. Simple Echo server implementation app/src/main/java/com/nio/EchoServer.java
  1. Build project using **gradle clean && gradle build**
  2. Run the server using **java -cp . app/src/main/java/com/nio/EchoServer.java**
  3. Connect to the server using **nc -v 0.0.0.0 5555**

## 2. Tcp server implementation of Reactor pattern app/src/main/java/com/nio/reactor/TcpServer.java
  1. Build project using **gradle clean && gradle build**
  2. Run the server using **java -cp . app/src/main/java/com/nio/reactor/TcpServer.java**
  3. Connect to the server using **nc -v 0.0.0.0 5555**
  
## 3. Tcp server implementation provided Data Flow Control app/src/main/java/com/nio/TcpDataFlowExample.java
  1. Build project using **gradle clean && gradle build**
  2. Run the server using **java -cp . app/src/main/java/com/nio/TcpDataFlowExample.java**
  3. Connect to the data channel **nc -v 0.0.0.0 5555**
  4. Connect to the control channel **nc -v 0.0.0.0 4444**
  
## References
### Basic
1. [ITT 2015 - Heinz Kabutz - The Multi-threading, Non Blocking IO](https://www.youtube.com/watch?v=uKc0Gx_lPsg&ab_channel=IstanbulTechTalks "ITT 2015 - Heinz Kabutz - The Multi-threading, Non Blocking IO")
2. [Java NIO Tutorial](https://jenkov.com/tutorials/java-nio/index.html "Java NIO Tutorial")
3. [Scalable IO in Java](https://gee.cs.oswego.edu/dl/cpjslides/nio.pdf "Scalable IO in Java")
### Advanced
5. [Queueing in the Linux Network Stack](https://www.linuxjournal.com/content/queueing-linux-network-stack "Queueing in the Linux Network Stack")
6. [Multi-queue improvements in Linux kernel Ethernet driver mvneta](https://bootlin.com/blog/multi-queue-improvements-in-linux-kernel-ethernet-mvneta/ "Multi-queue improvements in Linux kernel Ethernet driver mvneta")
7. [Java NIO - Reactor](https://github.com/kasun04/nio-reactor "Java NIO - Reactor")
8. [SSL Introduction with Sample Transaction and Packet Exchange](https://www.cisco.com/c/en/us/support/docs/security-vpn/secure-socket-layer-ssl/116181-technote-product-00.html "SSL Introduction with Sample Transaction and Packet Exchange")
9. [Java SSLEngine](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/samples/sslengine/SSLEngineSimpleDemo.java "Java SSLEngine")
