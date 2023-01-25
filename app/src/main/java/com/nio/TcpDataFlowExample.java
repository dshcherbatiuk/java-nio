package com.nio;

import com.nio.tcp.entities.Port;
import com.nio.tcp.entities.TcpConnectionData;
import com.nio.tcp.entities.TcpDataFlowConnector;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public final class TcpDataFlowExample {

	public static final String HOSTNAME = "0.0.0.0";

	private TcpDataFlowExample() {
	}

	public static void main(final String... args) throws IOException {
		System.out.printf("Tcp Data Flow Example started at %s:%s%n", HOSTNAME, Port.getAvailablePorts());

		final TcpConnectionData connectionData = new TcpConnectionData(HOSTNAME);
		final TcpDataFlowConnector tcpDataFlowConnector = new TcpDataFlowConnector(connectionData);

		while (!Thread.currentThread().isInterrupted()) {
			System.out.printf("Wait new events..%n");
			connectionData.getSelector().select();

			for (SelectionKey selectionKey : connectionData.getSelector().selectedKeys()) {

				if (selectionKey.isAcceptable()) {
					tcpDataFlowConnector.acceptNewConnection(selectionKey);
				}

				if (tcpDataFlowConnector.isCloseConnection(selectionKey)) {
					tcpDataFlowConnector.closeConnection(selectionKey);
					return;
				}

				if (selectionKey.isReadable()) {
					tcpDataFlowConnector.readableAction(selectionKey);
				}
			}
		}
	}

}
