package com.nio.tcp.singleton;

import com.nio.tcp.exception.SelectorCreationException;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorSingleton {

    private static Selector selectorInstance;

    private SelectorSingleton() {
    }

    public static Selector getInstance() {
        if (selectorInstance == null) {
            try {
                selectorInstance = Selector.open();
            } catch (IOException ex) {
                throw new SelectorCreationException(ex.getMessage());
            }
        }
        return selectorInstance;
    }
}
