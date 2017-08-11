package org.lastmayday.async.client.impl;

import com.alibaba.fastjson.JSON;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lastmayday.async.common.model.AsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send long polling request to server.
 */
public class ClientService {
    private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);
    private static final String LONG_PULLING_URL = "/register";
    private static final int TIMEOUT = 30000;
    private final ScheduledExecutorService service;
    private final AsyncInstance instance;
    private CloseableHttpClient httpclient;
    private URI fetchUri;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ClientService(AsyncInstance instance) {
        this.instance = instance;
        this.httpclient = HttpClients.createDefault();
        String fetchUrl = instance.getProperty("async.server") + LONG_PULLING_URL;
        try {
            fetchUri = new URIBuilder(fetchUrl).addParameter("id", instance.getName()).build();
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }
        service = Executors.newScheduledThreadPool(1, (r) -> {
            Thread t = new Thread(r);
            t.setName("org.lastmayday.client.pulling." + instance.getName());
            t.setDaemon(true);
            return t;
        });
        service.scheduleAtFixedRate(() -> {
            fetchMessage();
        }, 1, 1, TimeUnit.MILLISECONDS);
    }

    private void fetchMessage() {
        try {
            HttpGet httpGet = new HttpGet(fetchUri);
            httpGet.setConfig(RequestConfig.custom().setConnectTimeout(TIMEOUT).build());
            CloseableHttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (200 != statusLine.getStatusCode()) {
                LOG.warn("status error: {}", statusLine.getStatusCode());
                return;
            }
            String res = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (res.isEmpty()) { // timeout will response empty string
                return;
            }
            AsyncResponse data = JSON.parseObject(res, AsyncResponse.class);
            String time = new Timestamp(data.getTimestamp()).toLocalDateTime().format(dateTimeFormatter);
            LOG.info("new message! \n{} {}: {}", time, data.getSrcClient(), data.getContent());
            fireListeners(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fireListeners(AsyncResponse response) {
        List<MessageListener> listeners = instance.getMessageListeners();
        for (MessageListener listener : listeners) {
            if (listener.getExecutor() != null) {
                listener.getExecutor().execute(() -> {
                    listener.onReceiveMessage(response);
                });
            } else {
                listener.onReceiveMessage(response);
            }
        }
    }
}
