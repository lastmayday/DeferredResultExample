package org.lastmayday.async.client.impl;

import java.util.concurrent.Executor;
import org.lastmayday.async.common.model.AsyncResponse;

public interface MessageListener {
    Executor getExecutor();

    void onReceiveMessage(final AsyncResponse response);
}
