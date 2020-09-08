package com.example.todo.model;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Tab {

    @PrimaryKey (autoGenerate = true)
    private int tabId;

    private String tabTitle;

    @Ignore
    private List<ToDo> toDoList = new ArrayList<>();

    public Tab(String tabTitle){
        this.tabTitle = tabTitle;
    }

    //  getter and setter

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public List<ToDo> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    @Override
    public String toString() {
        return "Tab{" +
                "toDoListSize= " + toDoList.size() +
                ", tabId=" + tabId +
                ", tabTitle='" + tabTitle + '\'' +
                ", toDoList=" + toDoList +
                '}';
    }
}
