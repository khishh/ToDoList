package com.example.todo.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class TabToDoDao {

    private static final String TAG = TabToDoDao.class.getSimpleName();

    @Insert
    public abstract List<Long> insertTab(Tab... tab);

    @Insert
    public abstract List<Long> insertToDoList(ToDo... toDoList);

    @Delete
    public abstract void deleteTab(Tab... tab);

    @Delete
    public abstract void deleteToDos(ToDo... toDos);

    @Update
    public abstract void updateTab(Tab... tab);

    @Update
    public abstract void updateToDoList(ToDo... toDos);

    @Query("SELECT * FROM Tab")
    public abstract LiveData<List<Tab>> getAllLiveTab();

    @Query("SELECT * FROM ToDo where toDoOwnerId = :toDoOwnerId")
    public abstract List<ToDo> getToDoList(int toDoOwnerId);

    @Query("SELECT * FROM ToDo where toDoOwnerId = :toDoOwnerId")
    public abstract LiveData<List<ToDo>> getLiveToDoList(int toDoOwnerId);

    public void insertToDoWithTab(Tab tab){

        int tabId = insertTab(tab).get(0).intValue();
        tab.setTabId(tabId);

        List<ToDo> toDoList = tab.getToDoList();

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoOwnerId(tab.getTabId());
        }

        List<Long> toDoIds = insertToDoList(toDoList.toArray(new ToDo[0]));

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoId(toDoIds.get(i).intValue());
        }
    }
}
