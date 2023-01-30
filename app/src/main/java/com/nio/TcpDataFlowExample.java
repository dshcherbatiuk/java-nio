package com.nio;

import com.nio.context.DataFlowContext;
import com.nio.context.DataFlowServer;
import com.nio.event.manager.SelectionKeyEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public final class TcpDataFlowExample {

	private static final String HOSTNAME = "0.0.0.0";

	private final DataFlowServer dataFlowServer;
	private final Selector selector;
	private final SelectionKeyEventManager selectionKeyEventManager;

	private final Logger logger = LoggerFactory.getLogger(TcpDataFlowExample.class);

	public TcpDataFlowExample(final DataFlowServer dataFlowServer,
							  final Selector selector,
							  final SelectionKeyEventManager selectionKeyEventManager) {
		this.dataFlowServer = dataFlowServer;
		this.selector = selector;
		this.selectionKeyEventManager = selectionKeyEventManager;
	}

	public void start(final String hostName) throws IOException {
		dataFlowServer.bootServer(hostName);

		while (!Thread.currentThread().isInterrupted()) {
			logger.info("Wait new events..%n");
			selector.select();

			final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			iterator.forEachRemaining(selectionKey -> {
				selectionKeyEventManager.handle(selectionKey);
				iterator.remove();
			});
		}
	}

	public static void main(final String... args) throws Exception {
		final TcpDataFlowExample tcpDataFlowExample = new TcpDataFlowExample(
				DataFlowContext.dataFlowServer(),
				DataFlowContext.selector(),
				DataFlowContext.selectionKeyEventManager()
		);
		tcpDataFlowExample.start(HOSTNAME);
	}
}
