package com.nio;

import java.nio.ByteBuffer;

//moved data processing logic to separate class to ensure single-responsibility principle
//allows easily change processing logic by implementing another DataProcessor
public class CaseSwitcherProcessor implements DataProcessor {

    @Override
    public ByteBuffer process(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) Character.toUpperCase(buffer.get(i)));
        }
        return buffer;
    }
}
