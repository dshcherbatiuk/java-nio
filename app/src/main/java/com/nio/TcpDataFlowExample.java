package com.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

public final class TcpDataFlowExample {

    public static final String HOSTNAME = "0.0.0.0";
    private static final int byteBufferCapacity = 65535;
    private final String hostName;
    private final int controlPort;
    private final int dataPort;
    private final int[] ports;
    private Selector selector;
    private ByteBuffer buffer;
    private ClientHelper clientHelper;

    private TcpDataFlowExample(String hostName, int controlPort, int dataPort) {
        this.hostName = hostName;
        this.controlPort = controlPort;
        this.dataPort = dataPort;
        this.ports = new int[]{controlPort, dataPort};
    }

    public static void main(final String... args) throws Exception {
        new TcpDataFlowExample(HOSTNAME, 5555, 4444).startEvents();
    }

    public void startEvents() throws IOException {
        System.out.printf("Tcp Data Flow Example started at %s:%s%n", hostName, Arrays.toString(ports));
        selector = Selector.open();
        clientHelper = new ClientHelper(selector);
        buffer = ByteBuffer.allocate(byteBufferCapacity);
        registerPorts();

        while (!Thread.currentThread().isInterrupted()) {
            processNewEvents();
        }
    }

    private void processNewEvents() throws IOException {
        System.out.printf("Wait new events..%n");
        selector.select();
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        iterator.forEachRemaining(selectionKey -> {
            try {
                if (selectionKey.isAcceptable()) {
                    clientHelper.registerClient(selectionKey);
                }
                if (selectionKey.isReadable()) {
                    if (handleReadEvent(selectionKey)) return;
                }
                iterator.remove();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean handleReadEvent(SelectionKey selectionKey) throws IOException {
        System.out.println("Handle READ event");
        final SocketChannelWrapper client = new CustomSocketChannel((SocketChannel) selectionKey.channel());

        if (client.read(buffer) == -1) {
            client.closeChannel(selector);
            return true;
        }

        buffer.flip();
        int localPort = client.getLocalPort();

        if (controlPort == localPort) {
            writeToBuffer();
            client.write(buffer);
        } else if (dataPort == localPort) {
            clientHelper.registerEvents(client, getCommand(), controlPort);
        }
        buffer.clear();
        return false;
    }

    private String getCommand() {
        return StandardCharsets.UTF_8.decode(buffer).toString().trim().toLowerCase();
    }

    private void registerPorts() throws IOException {
        for (final int port : ports) {
            clientHelper.registerPort(hostName, port);
        }
    }

    private void writeToBuffer() {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
    }

}
