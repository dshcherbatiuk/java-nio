package com.nio;

import com.nio.example.data.TcpService;

import java.util.Arrays;

public final class TcpDataFlowExample {

	public static final String HOSTNAME = "0.0.0.0";
	public static final int[] PORTS = new int[] { 5555, 4444 };

	private TcpDataFlowExample() {
	}

	public static void main(final String... args) throws Exception {
		System.out.printf("Tcp Data Flow Example started at %s:%s%n", HOSTNAME, Arrays.toString(PORTS));
		TcpService tcpService = new TcpService();
		tcpService.run();
	}
}
