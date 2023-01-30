package com.nio;

import com.nio.eventhadler.AcceptEventHandler;
import com.nio.eventhadler.ReadEventHandler;
import com.nio.eventhadler.WriteEventHandler;
import com.nio.poller.PollerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;

public final class TcpDataFlowExample {
	private static final Logger LOGGER = LoggerFactory.getLogger(TcpDataFlowExample.class);

	public static final String HOSTNAME = "127.0.0.1";
	public static final int DATA_PORT = 5555;
	public static final int CONTROL_PORT = 4444;
	public static final int[] PORTS = new int[]{DATA_PORT, CONTROL_PORT};
	private final PollerImpl poller;

	private TcpDataFlowExample() throws IOException {
		this.poller = new PollerImpl();
	}

	public static void main(String[] args) throws IOException {
		TcpDataFlowExample tcpDataFlowExample = new TcpDataFlowExample();
		tcpDataFlowExample.start();
	}

	private void start() throws IOException {
		LOGGER.info("Tcp Data Flow Example started at" + HOSTNAME + " " + Arrays.toString(PORTS));

		for (final int port : PORTS) {
			final ServerSocketChannel serverSocket = ServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress(HOSTNAME, port));
			serverSocket.configureBlocking(false);
			poller.registerChannel(serverSocket, SelectionKey.OP_ACCEPT);
		}

		poller.registerEvent(SelectionKey.OP_ACCEPT, new AcceptEventHandler());
		poller.registerEvent(SelectionKey.OP_READ, new ReadEventHandler());
		poller.registerEvent(SelectionKey.OP_WRITE, new WriteEventHandler());

		poller.poll();
	}
}
