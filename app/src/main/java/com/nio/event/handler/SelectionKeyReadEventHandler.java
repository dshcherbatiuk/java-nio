package com.nio.event.handler;

import com.nio.exception.ConnectionReadingException;
import com.nio.strategy.ChannelProcessingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

public class SelectionKeyReadEventHandler implements SelectionKeyEventHandler {

    private static final Integer CLOSE_CONNECTION_CODE = -1;

    private final List<ChannelProcessingStrategy> channelProcessingStrategies;
    private final Selector selector;
    private final ByteBuffer buffer;
    private final Logger logger = LoggerFactory.getLogger(SelectionKeyReadEventHandler.class);

    public SelectionKeyReadEventHandler(final List<ChannelProcessingStrategy> channelProcessingStrategies,
                                        final Selector selector,
                                        final ByteBuffer buffer) {
        this.channelProcessingStrategies = channelProcessingStrategies;
        this.selector = selector;
        this.buffer = buffer;
    }

    @Override
    public void handle(final SelectionKey selectionKey) {
        try {
            logger.info("Handle READ event");
            final SocketChannel client = (SocketChannel) selectionKey.channel();
            final int read = client.read(buffer);

            if (read == CLOSE_CONNECTION_CODE) {
                closeConnection(client);
                return;
            }

            buffer.flip();
            channelProcessingStrategies.stream()
                    .filter(channelProcessingStrategy -> channelProcessingStrategy.getChannelPort().equals(client.socket().getLocalPort()))
                    .findFirst()
                    .ifPresent(channelProcessingStrategy -> channelProcessingStrategy.process(buffer, client));
            buffer.clear();
        } catch (final IOException e) {
            throw new ConnectionReadingException(e.getMessage());
        }
    }

    private void closeConnection(final SocketChannel client) throws IOException {
        client.close();
        client.keyFor(selector).cancel();
        logger.info("The connection was closed: {}", client);
    }
}
