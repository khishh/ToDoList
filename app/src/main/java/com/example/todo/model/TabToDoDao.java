package com.example.todo.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class TabToDoDao {

    private static final String TAG = "TabToDoDao";

    @Insert
    public abstract Long insertTab(Tab tab);

    @Insert
    public abstract List<Long> insertToDoList(List<ToDo> toDoList);

    @Delete
    public abstract void deleteTab(Tab tab);

    @Delete
    public abstract void deleteToDos(List<ToDo> toDos);

    @Update
    public abstract void updateTab(Tab tab);

    @Update
    public abstract void updateToDoList(List<ToDo> toDos);

    @Query("SELECT * FROM Tab")
    public abstract List<Tab> getAllTab();

    @Query("SELECT * FROM Tab")
    public abstract LiveData<List<Tab>> getAllLiveTab();

    @Query("DELETE FROM Tab where tabId = :tabId")
    public abstract  void deleteTabOfId(int tabId);

    @Query("DELETE FROM ToDo where toDoOwnerId = :toDoOwnerId")
    public abstract void deleteAllToDoOfId(int toDoOwnerId);

    @Query("SELECT * FROM Tab where tabIndex = :tabIndex")
    public abstract Tab getTab(int tabIndex);

    @Query("SELECT * FROM ToDo where toDoOwnerId = :toDoOwnerId")
    public abstract List<ToDo> getToDoList(int toDoOwnerId);

    @Query("SELECT * FROM ToDo where toDoOwnerId = :toDoOwnerId")
    public abstract LiveData<List<ToDo>> getLiveToDoList(int toDoOwnerId);

    public void insertToDoWithTab(Tab tab){

        int tabId = insertTab(tab).intValue();
        tab.setTabId(tabId);

        List<ToDo> toDoList = tab.getToDoList();

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoOwnerId(tab.getTabId());
        }

        List<Long> toDoIds = insertToDoList(toDoList);

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoId(toDoIds.get(i).intValue());
        }
    }

    public Tab getTabWithToDo(int tabIndex){
        Tab tab = getTab(tabIndex);
        List<ToDo> toDoList = getToDoList(tabIndex);
        tab.setToDoList(toDoList);
        return tab;
    }
}
