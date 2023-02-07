package com.nio;

import java.nio.ByteBuffer;

//introduced interface data processing logic so that processing logic can be easily replaced (Liskov substitution principle)
//also may cover interface segregation principle
public interface DataProcessor {
    ByteBuffer process(ByteBuffer buffer);
}
