package com.nio;

import com.nio.tcpdataflowexample.server.Server;
import com.nio.tcpdataflowexample.impl.ControllableToUpperCaseServer;

import java.util.Arrays;

public final class TcpDataFlowExample {

	private static final String HOSTNAME = "127.0.0.1";
	private static final int COMMAND_PORT = 4444;
	private static final int DATA_PORT = 5555;

	public static void main(final String... args) throws Exception {
		System.out.printf("Tcp Data Flow Example started at %s:%s%n", HOSTNAME, Arrays.toString(new int[]{COMMAND_PORT, DATA_PORT}));

		Server dataFlowTestTcpServer = new ControllableToUpperCaseServer(HOSTNAME, COMMAND_PORT, DATA_PORT);
		dataFlowTestTcpServer.start();

		System.out.println("Tcp Data Flow Example stopped");
	}
}
