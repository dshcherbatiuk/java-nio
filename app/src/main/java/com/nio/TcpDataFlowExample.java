package com.nio;

import com.nio.tcp.TcpDataFlow;

public final class TcpDataFlowExample {

	public static void main(final String... args) throws Exception {
		TcpDataFlow tcpDataFlow = new TcpDataFlow("0.0.0.0", 5555, 4444);
		tcpDataFlow.start();
	}
}
