package com.example.todo;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.Tab;
import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.List;

public class ToDoRepository {

    private final static String TAG = ToDoRepository.class.getSimpleName();


    //    private TabToDoDao;
    private static ToDoRepository instance;

    private TabToDoDao dao;

    private LiveData<List<Tab>> tabList = new MutableLiveData<>();
    private LiveData<List<List<ToDo>>> toDoList = new MutableLiveData<>();

    private ToDoRepository(Application application){
        TabToDoDataBase dataBase = TabToDoDataBase.getInstance(application);
        dao = dataBase.tabToDoDao();
        tabList = dao.getAllLiveTab();

        for(Tab tab : tabList.getValue()){
            dao.getLiveToDoList(tab.getTabId());
        }
    }

    public static ToDoRepository getInstance(Application application){
        if(instance == null){
            instance = new ToDoRepository(application);
        }
        return instance;
    }

}
