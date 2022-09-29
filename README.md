# java-nio

Follow the link to find [Presentation](https://www.youtube.com/watch?v=Qm9hhPFelvg&ab_channel=IntelliasInside "Presentation")

Follow the link to find [Javeloper Presentation](https://www.youtube.com/watch?v=gS2rSsw6DZ4&ab_channel=KonferencjaOnline-Canal3 "Javeloper Presentation")

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
  
  The idea of the project to show how we can manipulate with TCP Flow Control using Java Nio. 
  There **data** and **control** channels. The data channel receive inbound data and convert it to UPPERCASE (just for fun). The control channel has 2 commands **stop-read** and **start-read** data to manipulate with data in the data channel.
  When we send stop-read command to the control channel the data channel will stop receive inbound data. In such case we can get TCP Window Size to 0. To start read data again we need to send start-read command and after that all data are contained to the queue will be received by the data channel.
  
  To send huge amount of data you can generate it here https://json-generator.com
  
## References
### Basic
1. [ITT 2015 - Heinz Kabutz - The Multi-threading, Non Blocking IO](https://www.youtube.com/watch?v=uKc0Gx_lPsg&ab_channel=IstanbulTechTalks "ITT 2015 - Heinz Kabutz - The Multi-threading, Non Blocking IO")
2. [Java NIO Tutorial](https://jenkov.com/tutorials/java-nio/index.html "Java NIO Tutorial")
3. [Scalable IO in Java](https://gee.cs.oswego.edu/dl/cpjslides/nio.pdf "Scalable IO in Java")
### Advanced
5. [Queueing in the Linux Network Stack](https://www.linuxjournal.com/content/queueing-linux-network-stack "Queueing in the Linux Network Stack")
6. [Multi-queue improvements in Linux kernel Ethernet driver mvneta](https://bootlin.com/blog/multi-queue-improvements-in-linux-kernel-ethernet-mvneta/ "Multi-queue improvements in Linux kernel Ethernet driver mvneta")
7. [TCP/IP Illustrated, Volume 1: The Protocols, 2nd Edition](https://www.oreilly.com/library/view/tcpip-illustrated-volume/9780132808200 "TCP/IP Illustrated, Volume 1: The Protocols, 2nd Edition")
8. [Java NIO - Reactor](https://github.com/kasun04/nio-reactor "Java NIO - Reactor")
9. [SSL Introduction with Sample Transaction and Packet Exchange](https://www.cisco.com/c/en/us/support/docs/security-vpn/secure-socket-layer-ssl/116181-technote-product-00.html "SSL Introduction with Sample Transaction and Packet Exchange")
10. [Java SSLEngine](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/samples/sslengine/SSLEngineSimpleDemo.java "Java SSLEngine")
11. [Performance and scalability analysis of
Java IO and NIO based server models,
their implementation and comparison](https://s3-eu-central-1.amazonaws.com/ucu.edu.ua/wp-content/uploads/sites/8/2019/12/Petro-Karabyn.pdf "Performance and scalability analysis of
Java IO and NIO based server models,
their implementation and comparison")
