package com.nio.reactor;

import com.nio.reactor.event.AcceptEventHandler;
import com.nio.reactor.event.ReadEventHandler;
import com.nio.reactor.event.WriteEventHandler;
import com.nio.reactor.poller.NetworkPoller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public final class TcpServer {

	public static final String HOSTNAME = "0.0.0.0";
	public static final int PORT = 5555;

	private final NetworkPoller networkPoller;
	private final String hostname;
	private final int port;

	public TcpServer(final String hostname, final int port) throws IOException {
		this.hostname = hostname;
		this.port = port;
		this.networkPoller = new NetworkPoller();
	}

	public void start() throws Exception {
		final ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress(hostname, port));
		serverSocket.configureBlocking(false);
		networkPoller.registerChannel(SelectionKey.OP_ACCEPT, serverSocket);

		networkPoller.registerEventhandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler(networkPoller.getSelector()));
		networkPoller.registerEventhandler(SelectionKey.OP_READ, new ReadEventHandler(networkPoller.getSelector()));
		networkPoller.registerEventhandler(SelectionKey.OP_WRITE, new WriteEventHandler(networkPoller.getSelector()));

		networkPoller.poll();
	}

	public static void main(String... args) throws Exception {
		System.out.printf("Reactor Pattern Example started at %s:%s%n", HOSTNAME, PORT);

		final TcpServer tcpServer = new TcpServer(HOSTNAME, PORT);
		tcpServer.start();
	}
}
