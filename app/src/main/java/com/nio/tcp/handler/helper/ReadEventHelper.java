package com.nio.tcp.handler.helper;

import com.nio.tcp.exception.ReadConnectionException;
import com.nio.tcp.model.ServerPort;
import com.nio.tcp.processor.ChannelProcessor;
import com.nio.tcp.processor.ControlChannelProcessor;
import com.nio.tcp.processor.DataChannelProcessor;
import com.nio.tcp.singleton.SelectorSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

public class ReadEventHelper implements EventHelper {

    private static final Integer CLOSE_CONNECTION_CODE = -1;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadEventHelper.class);
    private static EventHelper readEventHelperInstance;
    private final List<ChannelProcessor> channelProcessors;
    private final Selector selector;
    private final ByteBuffer buffer;

    private ReadEventHelper() {
        this.channelProcessors = Arrays.asList(ControlChannelProcessor.getInstance(), DataChannelProcessor.getInstance());
        this.selector = SelectorSingleton.getInstance();
        this.buffer = ByteBuffer.allocate(65535);
    }

    public static EventHelper getInstance() {
        if (readEventHelperInstance == null) {
            readEventHelperInstance = new ReadEventHelper();
        }
        return readEventHelperInstance;
    }

    @Override
    public void processEvent(SelectionKey selectionKey) {
        try {
            LOGGER.info("Handle READ event");
            SocketChannel client = (SocketChannel) selectionKey.channel();
            int read = client.read(buffer);

            if (read == CLOSE_CONNECTION_CODE) {
                closeConnection(client);
                return;
            }

            buffer.flip();

            ServerPort clientPort = ServerPort.fromValue(client.socket().getLocalPort());

            if (clientPort != null) {
                channelProcessors.stream()
                        .filter(channelProcessor -> channelProcessor.getPort().equals(clientPort))
                        .findFirst()
                        .ifPresent(channelProcessor -> channelProcessor.process(buffer, client));
                buffer.clear();
            }
        } catch (IOException e) {
            throw new ReadConnectionException(e.getMessage());
        }
    }

    private void closeConnection(SocketChannel client) throws IOException {
        client.close();
        client.keyFor(selector).cancel();
        LOGGER.info("The connection was closed: {}", client);
    }
}