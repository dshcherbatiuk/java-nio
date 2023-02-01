package com.nio.refactor.support;

import java.io.IOException;
import java.nio.ByteBuffer;
//import com.sun.tools.javac.util.ByteBuffer;

import java.nio.channels.SocketChannel;

public class Buffer {
    private final ByteBuffer buffer;

    public Buffer() {
        buffer = ByteBuffer.allocate(65535);
    }

    public void bufferWrite (SocketChannel client) throws IOException {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        client.write(buffer);
    }
    public void bufferFlip(){
        buffer.flip();
    }
    public void bufferClear(){
        buffer.clear();
    }
    public boolean bufferRead(SocketChannel client) throws IOException {
        return client.read(buffer) == -1;
    }
}
