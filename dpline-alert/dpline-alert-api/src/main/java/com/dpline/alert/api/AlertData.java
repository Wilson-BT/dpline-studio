package com.dpline.alert.api;

public class AlertData {
    private String title;
    private String content;

    public AlertData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public AlertData() {
    }

    public String getTitle() {
        return this.title;
    }

    public AlertData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public AlertData setContent(String content) {
        this.content = content;
        return this;
    }

}
