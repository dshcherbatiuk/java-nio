package com.nio.reactor.poller;

@FunctionalInterface
public interface Poller {
	void poll();
}
