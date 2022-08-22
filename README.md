# java-nio

There are 3 examples using Java Nio library:

## 1. Simple Echo server implementation app/src/main/java/com/nio/EchoServer.java
  1.1 Build project using **gradle clean && gradle build**<br>
  1.2 Run the server using **java -cp . app/src/main/java/com/nio/EchoServer.java**<br>
  1.3 Connect to the server using **nc -v 0.0.0.0 5555**<br>

## 2. Tcp server implementation of Reactor pattern app/src/main/java/com/nio/reactor/TcpServer.java
  1.1 Build project using **gradle clean && gradle build**<br>
  1.2 Run the server using **java -cp . app/src/main/java/com/nio/reactor/TcpServer.java**<br>
  1.3 Connect to the server using **nc -v 0.0.0.0 5555**<br>
  
## 3. Tcp server implementation provided Data Flow Control app/src/main/java/com/nio/TcpDataFlowExample.java
  1.1 Build project using **gradle clean && gradle build**<br>
  1.2 Run the server using **java -cp . app/src/main/java/com/nio/TcpDataFlowExample.java**<br>
  1.3 Connect to the data channel **nc -v 0.0.0.0 5555**<br>
  1.4 Connect to the control channel **nc -v 0.0.0.0 4444**<br>
