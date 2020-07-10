package com.example.todo.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ToDo {

    @PrimaryKey (autoGenerate = true)
    private int toDoId;

    private int toDoOwnerIndex;

    private String content;

    private boolean isDone;

    public ToDo(int toDoOwnerIndex, String content, boolean isDone){
        this.toDoOwnerIndex = toDoOwnerIndex;
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

    public int getToDoOwnerIndex() {
        return toDoOwnerIndex;
    }

    public void setToDoOwnerIndex(int toDoOwnerIndex) {
        this.toDoOwnerIndex = toDoOwnerIndex;
    }

    public int getToDoId() {
        return toDoId;
    }

    public void setToDoId(int toDoId) {
        this.toDoId = toDoId;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "toDoId=" + toDoId +
                ", toDoOwnerIndex=" + toDoOwnerIndex +
                ", content='" + content + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
