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
1. https://www.coverfire.com/articles/queueing-in-the-linux-network-stack/
2. https://bootlin.com/blog/multi-queue-improvements-in-linux-kernel-ethernet-mvneta/
3. https://github.com/kasun04/nio-reactor
4. https://gee.cs.oswego.edu/dl/cpjslides/nio.pdf
5. https://jenkov.com/tutorials/java-nio/index.html
6. https://www.cisco.com/c/en/us/support/docs/security-vpn/secure-socket-layer-ssl/116181-technote-product-00.html
7. https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/samples/sslengine/SSLEngineSimpleDemo.java
