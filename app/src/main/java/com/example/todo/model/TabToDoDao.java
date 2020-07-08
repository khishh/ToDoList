package com.example.todo.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class TabToDoDao {

    private static final String TAG = "TabToDoDao";

    @Insert
    public abstract void insertTab(Tab tab);

    @Insert
    public abstract void insertToDoList(List<ToDo> toDoList);

    @Query("SELECT * FROM Tab")
    public abstract List<Tab> getAllTab();

    @Query("SELECT * FROM Tab where tabId = :tabId")
    public abstract Tab getTab(int tabId);

    @Query("SELECT * FROM ToDo where toDoOwnerIndex = :toDoOwnerIndex")
    public abstract List<ToDo> getToDoList(int toDoOwnerIndex);

    public void insertToDoWithTab(Tab tab){
        List<ToDo> toDoList = tab.getToDoList();

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoOwnerIndex(tab.getTabId());
        }

        insertToDoList(toDoList);
        insertTab(tab);
    }

    public Tab getTabWithToDo(int tabId){
        Tab tab = getTab(tabId);
        List<ToDo> toDoList = getToDoList(tabId);
        tab.setToDoList(toDoList);
        return tab;
    }
}
