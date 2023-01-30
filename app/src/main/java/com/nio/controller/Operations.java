package com.nio.controller;

import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Operations {

    private static final Logger log = LoggerFactory.getLogger(Operations.class);

    private static Map<String, BiConsumer<Selector, Multimap<Integer, SocketChannel>>> commandMap = new HashMap<>();
    static {
        commandMap.put("stop-read",  (s, c) -> {log.info("Handle stop-read"); registerEvent(s, c, 0);});
        commandMap.put("start-read", (s, c) -> {log.info("Handle stop-read"); registerEvent(s, c, SelectionKey.OP_READ);});
    }

    private SocketChannel client;
    private ByteBuffer buffer;
    private Selector selector;
    private Multimap<Integer, SocketChannel> clients;


    public Operations(final SocketChannel client, final ByteBuffer buffer, Selector selector, Multimap<Integer, SocketChannel> clients) {
        this.client = client;
        this.buffer = buffer;
        this.selector = selector;
        this.clients = clients;
    }

    public void control() {
        final String command = StandardCharsets.UTF_8.decode(buffer).toString();
        commandMap.getOrDefault(command.toLowerCase(), (s, c) ->
        {
            try {
                final byte[] unknownCommand = String.format("Supported commands:%s",
                        String.join(",", commandMap.keySet())).getBytes(StandardCharsets.UTF_8);
                client.write(ByteBuffer.wrap(unknownCommand));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).accept(selector, clients);
    }

    public void write() {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        try {
            client.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) {
        clients.get(Channel.DATA_CHANNEL.getPort()).forEach(c -> {
            try {
                c.register(selector, op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
