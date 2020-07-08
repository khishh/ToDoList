package com.example.todo.ui.home.homefragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todo.model.ToDo;
import com.example.todo.model.ToDoCollection;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "HomeViewModel";

    private MutableLiveData<List<String>> tabTitles = new MutableLiveData<>();
    private MutableLiveData<List<ToDo>> doList = new MutableLiveData<>();
    private MutableLiveData<Integer> tabCount = new MutableLiveData<>();

    public HomeViewModel() {
        // set the initial value of tabSize and tabTitles
        tabCount.setValue(ToDoCollection.getInstance().getCollection().size());
        tabTitles.setValue(ToDoCollection.getInstance().getCollectionTitles());
    }

    public MutableLiveData<List<ToDo>> getDoList() {
        return doList;
    }

    public void setDoList(int position){
        doList.setValue(ToDoCollection.getInstance().getCollection().get(position));
    }

    public void updateTabCount(){
        tabCount.setValue(ToDoCollection.getInstance().getCollection().size());
    }

    public void incrementTabSize(String newTabTitle){
        ToDoCollection.getInstance().incrementSizeOfCollection(newTabTitle);
        tabCount.setValue(ToDoCollection.getInstance().getCollection().size());
    }

    public void hideUserInputField(){

    }

    public LiveData<Integer> getTabCount() {
        return tabCount;
    }

    public void setTabCount(Integer tabCount) {
        this.tabCount.setValue(tabCount);
    }

    public LiveData<List<String>> getTabTitles() {
        return tabTitles;
    }

    public void setTabTitles(List<String> tabTitles) {
        this.tabTitles.setValue(tabTitles);
    }

}