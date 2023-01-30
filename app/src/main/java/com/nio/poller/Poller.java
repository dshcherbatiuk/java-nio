package com.nio.poller;

import java.io.IOException;

public interface Poller {
	void poll() throws IOException;
}
