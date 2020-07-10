package com.example.todo.model;

import android.util.Log;

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
    public abstract void insertTab(Tab tab);

    @Insert
    public abstract void insertToDoList(List<ToDo> toDoList);

    @Update
    public abstract void updateTab(Tab tab);

    @Update
    public abstract  void updateToDoList(List<ToDo> toDos);

    @Query("SELECT * FROM Tab")
    public abstract List<Tab> getAllTab();

    @Query("DELETE FROM ToDo where toDoOwnerIndex = :toDoOwnerIndex")
    public abstract void deleteAllToDoOfIndex(int toDoOwnerIndex);

    @Query("SELECT * FROM Tab where tabIndex = :tabIndex")
    public abstract Tab getTab(int tabIndex);

    @Query("SELECT * FROM ToDo where toDoOwnerIndex = :toDoOwnerIndex")
    public abstract List<ToDo> getToDoList(int toDoOwnerIndex);

    public void insertToDoWithTab(Tab tab){
        List<ToDo> toDoList = tab.getToDoList();

        for(int i = 0; i < toDoList.size(); i++){
            toDoList.get(i).setToDoOwnerIndex(tab.getTabIndex());
            Log.d(TAG, tab.getTabIndex() + " " + toDoList.get(i).getToDoOwnerIndex());
        }

        Log.d(TAG, "Size of toDoList == " + toDoList.size());
        insertToDoList(toDoList);
        insertTab(tab);
    }

    public Tab getTabWithToDo(int tabIndex){
        Tab tab = getTab(tabIndex);
        List<ToDo> toDoList = getToDoList(tabIndex);
        tab.setToDoList(toDoList);
        return tab;
    }
}
