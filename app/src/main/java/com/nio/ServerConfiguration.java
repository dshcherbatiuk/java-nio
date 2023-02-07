package com.nio;

import java.nio.channels.Selector;

//interface segregation principle
public interface ServerConfiguration {

    void configureServerSocketChannel(Selector selector) throws Exception;
    String getHost();
    Integer getCommandPort();
    Integer getDataPort();

}
