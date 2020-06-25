package com.example.todo.model;

public class ToDo {

    private String content;
    private boolean isDone;

    public ToDo(String content, boolean isDone){
        this.content = content;
        this.isDone = isDone;
    }

    // accessor

    public String getContent() {
        return content;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
