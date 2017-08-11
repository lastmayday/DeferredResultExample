package org.lastmayday.async.server.service;

import org.lastmayday.async.common.model.AsyncResponse;
import org.lastmayday.async.server.model.AsyncResponseTask;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class AsyncService {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncService.class);
    private static int THREAD_NUM = 4;

    private ExecutorService service;
    private ConcurrentHashMap<String, DeferredResult> clients;
    private LinkedBlockingQueue<AsyncResponseTask> tasks = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        this.clients = new ConcurrentHashMap<>();
        this.service = Executors.newFixedThreadPool(THREAD_NUM);
        for (int i = 0; i < THREAD_NUM; i ++) {
            this.service.execute(new TaskThread());
        }
    }

    public void registerClient(String id, DeferredResult result) {
        clients.putIfAbsent(id, result);
    }

    public void removeClient(String id) {
        clients.remove(id);
    }

    public String sendMessage(String srcClient, String destClient, String content) {
        if (clients.get(destClient) == null) {
            return "Error: dest client is offline!";
        }
        DeferredResult result = clients.get(destClient);
        clients.remove(destClient);
        AsyncResponseTask task = new AsyncResponseTask(srcClient, content, result);
        boolean res = tasks.offer(task);
        return "message " + res;
    }

    public void sendResponse(AsyncResponseTask task) {
        DeferredResult result = task.getDeferredResult();
        AsyncResponse response = task.getResponse();
        result.setResult(JSONObject.toJSON(response));
    }

    private class TaskThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                AsyncResponseTask task;
                try {
                    task = tasks.take();
                    if (task != null) {
                        sendResponse(task);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
