package org.lastmayday.async.client.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncInstance {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncInstance.class);

    private String name;
    private Properties prop = new Properties();
    private ClientService clientService;
    private CopyOnWriteArrayList<MessageListener> listeners;

    public void init(String clientId) {
        this.setName(clientId);
        this.listeners = new CopyOnWriteArrayList<>();
        try {
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("application.properties");
            prop.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.clientService = new ClientService(this);
    }

    public void addListener(MessageListener listener) {
        this.listeners.add(listener);
    }

    public void setName(String clientId) {
        this.name = clientId;
    }

    public String getName() {
        return name;
    }

    public String getProperty(String key) {
        return this.prop.getProperty(key);
    }

    public List<MessageListener> getMessageListeners() {
        return this.listeners;
    }

    public void sendMessage(String destClient, String message) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = getProperty("async.server") + "/message";
            URI uri = new URIBuilder(url).addParameter("src", getName())
                .addParameter("dest", destClient)
                .addParameter("content", message)
                .build();
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOG.info("send message result: " + res);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
