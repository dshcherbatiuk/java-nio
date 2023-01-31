package com.nio.tcp.processor;

import com.google.common.collect.Multimap;
import com.nio.tcp.exception.ControlChannelProcessingException;
import com.nio.tcp.model.Command;
import com.nio.tcp.model.ServerPort;
import com.nio.tcp.singleton.SelectorSingleton;
import com.nio.tcp.singleton.SocketChannelsSingletone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static com.nio.tcp.model.Command.*;

public class ControlChannelProcessor implements ChannelProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlChannelProcessor.class);
    private static ChannelProcessor controlChannelProcessorInstance;
    private final Multimap<Integer, SocketChannel> socketChannels;
    private final Selector selector;

    private ControlChannelProcessor() {
        this.socketChannels = SocketChannelsSingletone.getInstance();
        this.selector = SelectorSingleton.getInstance();
    }

    public static ChannelProcessor getInstance() {
        if (controlChannelProcessorInstance == null) {
            controlChannelProcessorInstance = new ControlChannelProcessor();
        }
        return controlChannelProcessorInstance;
    }

    @Override
    public ServerPort getPort() {
        return ServerPort.CONTROL_CHANNEL_PORT;
    }

    @Override
    public void process(ByteBuffer buffer, SocketChannel client) {
        String stringCommand = StandardCharsets.UTF_8.decode(buffer).toString();
        Command command = fromValue(stringCommand.trim());
        if (command != null) {
            registerEvent(selector, socketChannels, command);
        } else {
            final byte[] unknownCommand = String.format("Supported commands:%n%s%n%s%n", STOP_READ.getValue(), START_READ.getValue())
                    .getBytes(StandardCharsets.UTF_8);
            try {
                client.write(ByteBuffer.wrap(unknownCommand));
            } catch (IOException e) {
                throw new ControlChannelProcessingException(e.getMessage());
            }
        }
    }

    private void registerEvent(Selector selector, Multimap<Integer, SocketChannel> socketChannels, Command command) {
        LOGGER.info("Handle {}", command.getValue());
        socketChannels.get(ServerPort.DATA_CHANNEL_PORT.getPortNumber()).forEach(c -> {
            try {
                c.register(selector, command.getSelectionKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}