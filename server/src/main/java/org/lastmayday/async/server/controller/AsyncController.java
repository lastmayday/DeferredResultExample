package org.lastmayday.async.server.controller;

import org.lastmayday.async.server.service.AsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
public class AsyncController {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncController.class);
    private static final long TIMEOUT = 30000L;

    @Autowired
    private AsyncService service;

    @RequestMapping("/register")
    public DeferredResult<String> registerClient(@RequestParam("id") String clientId) {
        final DeferredResult<String> deferredResult = new DeferredResult<>(TIMEOUT, "");
        deferredResult.onTimeout(()-> {
            service.removeClient(clientId);
            LOG.info("client[{}] timeout...", clientId);
        });
        service.registerClient(clientId, deferredResult);
        return deferredResult;
    }

    @RequestMapping("/message")
    public String sendMessage(@RequestParam("src") String srcClient,
                              @RequestParam("dest") String destClient,
                              @RequestParam("content") String content) {
        LOG.info("new message from {} to {}, content: {}", srcClient, destClient, content);
        String res = service.sendMessage(srcClient, destClient, content);
        return res;
    }

}
