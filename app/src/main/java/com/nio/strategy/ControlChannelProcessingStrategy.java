package com.nio.strategy;

import com.google.common.collect.Multimap;
import com.nio.DataFlowServerPort;
import com.nio.exception.ControlChannelProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ControlChannelProcessingStrategy implements ChannelProcessingStrategy {

    private static final String STOP_COMMAND = "stop-read";
    private static final String START_COMMAND = "start-read";

    private final Multimap<Integer, SocketChannel> clients;
    private final Selector selector;
    private final Logger logger = LoggerFactory.getLogger(ControlChannelProcessingStrategy.class);

    public ControlChannelProcessingStrategy(final Multimap<Integer, SocketChannel> clients, final Selector selector) {
        this.clients = clients;
        this.selector = selector;
    }

    @Override
    public Integer getChannelPort() {
        return DataFlowServerPort.CONTROL_CHANNEL_PORT.getPortNumber();
    }

    @Override
    public void process(final ByteBuffer buffer, final SocketChannel client) {
        final String command = StandardCharsets.UTF_8.decode(buffer).toString();
        if (STOP_COMMAND.equalsIgnoreCase(command.trim())) {
            logger.info("Handle stop-read");
            registerEvent(selector, clients, 0);
        } else if (START_COMMAND.equalsIgnoreCase(command.trim())) {
            logger.info("Handle start-read");
            registerEvent(selector, clients, SelectionKey.OP_READ);
        } else {
            final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", STOP_COMMAND, START_COMMAND)
                    .getBytes(StandardCharsets.UTF_8);
            try {
                client.write(ByteBuffer.wrap(unknownCommand));
            } catch (IOException e) {
                throw new ControlChannelProcessingException(e.getMessage());
            }
        }
    }

    private void registerEvent(final Selector selector, final Multimap<Integer, SocketChannel> clients, final int op) {
        clients.get(DataFlowServerPort.DATA_CHANNEL_PORT.getPortNumber()).forEach(c -> {
            try {
                c.register(selector, op);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
