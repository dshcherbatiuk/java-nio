package com.nio.refactor.support;

import com.google.common.collect.Multimap;
import com.nio.refactor.enums.Ports;
import com.nio.refactor.interfaces.RegisterEvent;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Optional;

public class Connector  {
    private ConnectionService connectionService;
    private Buffer buffer = new Buffer();
    public Connector(ConnectionService connectionService){
        this.connectionService = connectionService;
    }

    public void socketConnection(SelectionKey selectionKey) throws IOException {
        System.out.println("Connection...");
        ServerSocketChannel server = ((ServerSocketChannel) selectionKey.channel());
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        System.out.printf("New connection accepted: %s%n", client);
        connectionService.getClients().put(server.socket().getLocalPort(), client);
        client.register(connectionService.getSelector(), SelectionKey.OP_READ);
    }

    public void handleReadEvent(SelectionKey selectionKey) throws IOException {
        System.out.println("Handle READ event");
        SocketChannel client = (SocketChannel) selectionKey.channel();
        buffer.bufferFlip();
        Optional<Ports> portsOptional = Ports.getAdressOptional(client.socket().getLocalPort());
        buffer.bufferWrite(client);
        buffer.bufferClear();
    }

    public boolean checkConnection(SelectionKey selectionKey) throws IOException {
        return buffer.bufferRead((SocketChannel) selectionKey.channel());
    }
    public void closeConnection(SelectionKey selectionKey) {
        try(SocketChannel client = (SocketChannel) selectionKey.channel()) {
            client.close();
            client.keyFor(connectionService.getSelector()).cancel();
            System.out.printf("The connection was closed: %s%n", client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
