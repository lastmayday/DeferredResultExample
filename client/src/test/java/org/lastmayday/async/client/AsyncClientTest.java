package org.lastmayday.async.client;

import java.util.concurrent.Executor;
import org.lastmayday.async.client.impl.MessageListener;
import org.lastmayday.async.common.model.AsyncResponse;

public class AsyncClientTest {

    public static void main(String[] args) {
        String clientId = "client1";
        AsyncClient.register(clientId);
        AsyncClient.addListener(new MessageListener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void onReceiveMessage(AsyncResponse response) {
                System.out.println("success receive message. now we will send response.");
                AsyncClient.sendMessage(response.getSrcClient(), "hey yo!");
            }
        });
        while (true) {}
    }
}
