package org.lastmayday.async.server.model;

import org.lastmayday.async.common.model.AsyncResponse;
import org.springframework.web.context.request.async.DeferredResult;

public class AsyncResponseTask {

    private AsyncResponse response;
    private DeferredResult deferredResult;

    public AsyncResponseTask(String srcClient, String content, DeferredResult deferredResult) {
        this.response = new AsyncResponse(srcClient, content);
        this.deferredResult = deferredResult;
    }

    public void setDeferredResult(DeferredResult deferredResult) {
        this.deferredResult = deferredResult;
    }

    public DeferredResult getDeferredResult() {
        return deferredResult;
    }

    public void setResponse(AsyncResponse response) {
        this.response = response;
    }

    public AsyncResponse getResponse() {
        return response;
    }
}
