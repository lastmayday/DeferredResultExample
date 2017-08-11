package org.lastmayday.async.common.model;

public class AsyncResponse {
    private String srcClient;
    private String content;
    private long timestamp;

    public AsyncResponse(String srcClient, String content) {
        this.srcClient = srcClient;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public void setSrcClient(String srcClient) {
        this.srcClient = srcClient;
    }

    public String getSrcClient() {
        return srcClient;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
