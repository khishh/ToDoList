package com.example.todo.ui.home.itemfragment;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todo.model.ToDo;
import com.example.todo.model.ToDoCollection;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {

    private static final String TAG = "ItemViewModel";
    private MutableLiveData<List<ToDo>> doList = new MutableLiveData<>();
    private MutableLiveData<Integer> tabCount = new MutableLiveData<>();

    public ItemViewModel(){
        super();
        Log.d(TAG, "ItemViewModel created");
    }

    public MutableLiveData<List<ToDo>> getDoList() {
        return doList;
    }

    public void setToDoList(int position){
        doList.setValue(ToDoCollection.getInstance().getCollection().get(position));
    }

    public MutableLiveData<Integer> getTabCount() {
        return tabCount;
    }

    public void updateTabCount() {
        tabCount.setValue(ToDoCollection.getInstance().getCollection().size());
    }
}