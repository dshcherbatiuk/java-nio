package com.nio;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class TcpDataFlowExample {

    private TcpDataFlowExample() {
    }

    public static void main(final String... args) throws Exception {
        Properties p = readProperties();
        String hostname = Config.HOSTNAME.get(p);
        int controlChannelPort = Integer.parseInt(Config.CONTROL_CHANNEL_PORT.get(p));
        int dataChannelPort = Integer.parseInt(Config.DATA_CHANNEL_PORT.get(p));

        System.out.printf("Tcp Data Flow Example started at %s:[%d, %d]%n", hostname, controlChannelPort, dataChannelPort);

        final Selector selector = Selector.open();
        final List<Channel> channels = Arrays.asList(
                new ControlChannel(hostname, controlChannelPort, selector),
                new DataChannel(hostname, dataChannelPort, selector)
        );
        final ByteBuffer buffer = ByteBuffer.allocate(65535);
        final Collection<SocketChannel> clients = new ArrayList<>();

        while (!Thread.currentThread().isInterrupted()) {
            System.out.printf("Waiting for new events..%n");
            selector.select();

            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            iterator.forEachRemaining(selectionKey -> {
                try {
                    if (selectionKey.isAcceptable()) {
                        Map.Entry<Integer, SocketChannel> entry = accept(selector, selectionKey);
                        if (entry.getKey() == dataChannelPort) {
                            clients.add(entry.getValue());
                        }
                    }

                    if (selectionKey.isReadable()) {
                        final SocketChannel client = read(selector, buffer, selectionKey);
                        if (client == null) return;
                        channels.stream()
                                .filter(channel -> Objects.equals(client.socket().getLocalPort(), channel.getPort()))
                                .findFirst()
                                .ifPresent(channel -> {
                                    try {
                                        channel.handle(buffer, client, clients);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                        buffer.clear();
                    }
                    iterator.remove();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static SocketChannel read(Selector selector, ByteBuffer buffer, SelectionKey selectionKey) throws IOException {
        System.out.println("Handle READ event");
        final SocketChannel client = (SocketChannel) selectionKey.channel();
        final int read = client.read(buffer);

        if (read == -1) {
            client.close();
            client.keyFor(selector).cancel();
            System.out.printf("The connection was closed: %s%n", client);
            return null;
        }

        buffer.flip();
        return client;
    }

    private static Map.Entry<Integer, SocketChannel> accept(Selector selector, SelectionKey selectionKey) {
        try {
            ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
            System.out.println("Handle READ event");
            final SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            System.out.printf("New connection accepted: %s%n", client);
            return new AbstractMap.SimpleEntry<>(server.socket().getLocalPort(), client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties readProperties() {
        try (InputStream is = TcpDataFlowExample.class.getResourceAsStream("application.conf")) {
            Properties properties = new Properties();
            if (Objects.isNull(is)) {
                System.out.println("application.conf is not found. Using defaults");
            } else {
                properties.load(is);
            }
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    abstract static class Channel {

        final int port;
        final Selector selector;

        Channel(String hostname, int port, Selector selector) {
            this.port = port;
            this.selector = selector;
            try {
                ServerSocketChannel serverSocket = ServerSocketChannel.open();
                serverSocket.bind(new InetSocketAddress(hostname, port));
                serverSocket.configureBlocking(false);
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public int getPort() {
            return port;
        }

        public abstract void handle(ByteBuffer buffer, SocketChannel client, Collection<SocketChannel> clients) throws IOException;

    }

    static class ControlChannel extends Channel {

        ControlChannel(String hostname, int port, Selector selector) {
            super(hostname, port, selector);
        }

        @Override
        public void handle(ByteBuffer buffer, SocketChannel client, Collection<SocketChannel> clients) throws IOException {
            Optional<Command> command = Command.get(StandardCharsets.UTF_8.decode(buffer).toString().trim());
            if (command.isPresent()) {
                command.get().execute(selector, clients);
            } else {
                byte[] unknownCommand = String.format("Supported commands:%n%s%n", Joiner.on("\n").join(Command.supportedCommands()))
                        .getBytes(StandardCharsets.UTF_8);
                client.write(ByteBuffer.wrap(unknownCommand));
            }
        }

    }

    static class DataChannel extends Channel {

        DataChannel(String hostname, int port, Selector selector) {
            super(hostname, port, selector);
        }

        @Override
        public void handle(ByteBuffer buffer, SocketChannel client, Collection<SocketChannel> clients) throws IOException {
            for (int i = 0; i < buffer.limit(); i++) {
                buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
            }
            client.write(buffer);
        }

    }

    enum Config {

        HOSTNAME("hostname", "0.0.0.0"),
        DATA_CHANNEL_PORT("data.channel.port", "5555"),
        CONTROL_CHANNEL_PORT("control.channel.port", "4444");

        final String propertyKey;
        final String defaultValue;

        Config(String propertyKey, String defaultValue) {
            this.propertyKey = propertyKey;
            this.defaultValue = defaultValue;
        }

        public String get(Properties properties) {
            return properties.getProperty(propertyKey, defaultValue);
        }

    }

    enum Command {

        START_READ("start-read", SelectionKey.OP_READ),
        STOP_READ("stop-read", 0);

        final String commandKey;
        final int operationBit;

        Command(String commandKey, int operationBit) {
            this.commandKey = commandKey;
            this.operationBit = operationBit;
        }

        public static Optional<Command> get(String command) {
            return Arrays.stream(Command.values())
                    .filter(c -> c.commandKey.equalsIgnoreCase(command))
                    .findFirst();
        }

        public static List<String> supportedCommands() {
            return Arrays.stream(Command.values())
                    .map(command -> command.commandKey)
                    .collect(Collectors.toList());
        }

        public void execute(Selector selector, Collection<SocketChannel> clients) {
            System.out.printf("Handle %s%n", commandKey);
            clients.forEach(socketChannel -> {
                try {
                    socketChannel.register(selector, operationBit);
                } catch (ClosedChannelException ignored) {
                }
            });
        }

    }


}
