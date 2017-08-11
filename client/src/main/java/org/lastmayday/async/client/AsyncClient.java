package org.lastmayday.async.client;

import org.lastmayday.async.client.impl.AsyncInstance;
import org.lastmayday.async.client.impl.MessageListener;

public class AsyncClient {

    public static final AsyncInstance instance = new AsyncInstance();

    public static void register(String clientId) {
        instance.init(clientId);
    }

    public static void addListener(MessageListener listener) {
        instance.addListener(listener);
    }

    public static void sendMessage(String destClient, String message) {
        instance.sendMessage(destClient, message);
    }
}
