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

    private int tabIndex;

    private String tabTitle;

    @Ignore
    private List<ToDo> toDoList = new ArrayList<>();

    public Tab(int tabIndex, String tabTitle){
        this.tabIndex = tabIndex;
        this.tabTitle = tabTitle;
    }

    //  getter and setter

    public int getTabId() {
        return tabId;
    }

    public void setTabId(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
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
                ", tabIndex=" + tabIndex +
                ", tabTitle='" + tabTitle + '\'' +
                ", toDoList=" + toDoList +
                '}';
    }
}
