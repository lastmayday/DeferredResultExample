package org.lastmayday.async.client;

public class AsyncClientTest2 {
    public static void main(String[] args) throws InterruptedException {
        String clientId = "client2";
        AsyncClient.register(clientId);
        Thread.sleep(60000);
        AsyncClient.sendMessage("client1", "hello world");
        while (true) {}
    }
}
