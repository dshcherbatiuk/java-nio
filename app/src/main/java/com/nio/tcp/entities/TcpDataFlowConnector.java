package com.nio.tcp.entities;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TcpDataFlowConnector {

    private final TcpConnectionData data;

    public TcpDataFlowConnector(TcpConnectionData connectionData) {
        this.data = connectionData;
    }

    public void acceptNewConnection(SelectionKey selectionKey) {
        System.out.println("Handle new connection");
        try(ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel())) {
            final SocketChannel socketChannel = server.accept();
            socketChannel.configureBlocking(false);
            System.out.printf("New connection accepted: %s%n", socketChannel);
            data.getClients().put(server.socket().getLocalPort(), socketChannel);
            socketChannel.register(data.getSelector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readableAction(SelectionKey selectionKey) {
        System.out.println("Handle READ event");

        try(SocketChannel client = (SocketChannel) selectionKey.channel()) {
            data.getBuffer().flip();
            Optional<Port> portOptional = Port.getPort(client.socket().getLocalPort());
            if (portOptional.isPresent()) {
                portOptional.get().execute(this, client);
            }
            data.getBuffer().clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCloseConnection(SelectionKey selectionKey) {
        try (SocketChannel client = (SocketChannel) selectionKey.channel()) {
            return client.read(data.getBuffer()) == -1;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(SelectionKey selectionKey) {
        try(SocketChannel client = (SocketChannel) selectionKey.channel()) {
            client.close();
            client.keyFor(data.getSelector()).cancel();
            System.out.printf("The connection was closed: %s%n", client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void read(SocketChannel client) throws IOException {
        final String commandName = StandardCharsets.UTF_8.decode(data.getBuffer()).toString().toLowerCase();
        Optional<Command> commandOptional = Command.getCommand(commandName);
        if (commandOptional.isPresent()) {
            Command command = commandOptional.get();
            System.out.println("Handle " + command.getCommandName());
            command.registerEvent(data.getSelector(), data.getClients());
        } else {
            Command.unsupportedCommandAction(client);
        }
    }

    public void write(SocketChannel client) throws IOException {
        for (int i = 0; i < data.getBuffer().limit(); i++) {
            data.getBuffer().put(i, (byte) Character.toUpperCase(data.getBuffer().get(i)));
        }
        client.write(data.getBuffer());
    }
}
