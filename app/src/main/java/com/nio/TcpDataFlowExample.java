package com.nio;

public final class TcpDataFlowExample {

	private TcpDataFlowExample() {
	}

	//extracted server logic to separate class to ensure single-responsibility principle
	public static void main(String[] args) throws Exception {
		ServerConfiguration config = new TcpServerConfiguration();
		DataProcessor dataProcessor = new CaseSwitcherProcessor();
		CommandProcessor commandProcessor = new CommandProcessorImpl();
		TcpDataFlowServer server = new TcpDataFlowServer(config, dataProcessor, commandProcessor);
		server.startServer();
	}
}
