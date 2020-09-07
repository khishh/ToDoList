package com.example.todo.ui.home.tabmanagementfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.Tab;
import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;


// *-----------------------------------------------------------
//  TabManagementViewModel class
//  Fields:
//      1. mTabList -> store all tabs retrieved from Room Database
//      2. retrievedTabFromDatabase -> the instance to RetrieveTabFromDatabase class extends to AsyncTask to get All Tabs from Room Database
//      3. updateTabIntoDatabase

public class TabManagementViewModel extends AndroidViewModel {

    private static final String TAG = "TabManagementViewModel";

    public enum ActionType{
        Add, Update, Delete, Edit
    }

    //  store all tabs retrieved from Room Database
    LiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<Void, Void, Void> updateTabIntoDatabase;

    private ActionType actionType;
    private boolean isDeleted = false;

    private List<Tab> modifiedTabs = new ArrayList<>();
    private List<Integer> preOrderedTabIds = new ArrayList<>();

    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    // default constructor
    public TabManagementViewModel(@NonNull Application application) {
        super(application);
    }

    // retrieving all Tabs in Room database by calling execute()
    public void retrieveTabList(){
        mTabList = dao.getAllLiveTab();
    }

    public void updateTabList(){
        updateTabIntoDatabase = new UpdateTabIntoDataBase2();
        updateTabIntoDatabase.execute();
    }

    public void deleteTab(Tab deleteTab){

        if(!isDeleted){
            isDeleted = true;
        }

        modifiedTabs.add(deleteTab);
        actionType = ActionType.Delete;
        updateTabList();
    }

    public void addNewTab(String newTabTitle){
        Tab newTab = new Tab(mTabList.getValue().size(), newTabTitle);
        modifiedTabs.add(newTab);
        actionType = ActionType.Add;
        updateTabList();
    }

    public void saveTabOrder(List<Tab> orderedTabList){

        // store the original Id's order
        for(Tab tab : mTabList.getValue()){
            preOrderedTabIds.add(tab.getTabId());
        }

        Log.e(TAG, preOrderedTabIds.toString());
        Log.e(TAG, "Orig ==== " + orderedTabList.toString());

        modifiedTabs.addAll(orderedTabList);
        actionType = ActionType.Update;
        updateTabIntoDatabase = new UpdateTabIntoDataBase2();
        updateTabIntoDatabase.execute();
    }

    public void editTabName(int positionToEdit, String newTabTitle){
        Tab editTab = mTabList.getValue().get(positionToEdit);
        editTab.setTabTitle(newTabTitle);
        modifiedTabs.add(editTab);

        actionType = ActionType.Edit;
        updateTabIntoDatabase = new UpdateTabIntoDataBase2();
        updateTabIntoDatabase.execute();
    }

    private class UpdateTabIntoDataBase2 extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            switch (actionType){

                case Delete:
                    Log.e(TAG, "Delete passed");
                    dao.deleteTab(modifiedTabs.toArray(new Tab[0]));
                    break;

                case Add:
                    Log.e(TAG, "Add passed");
                    dao.insertTab(modifiedTabs.toArray(new Tab[0]));
                    break;

                case Update:
                    Log.e(TAG, "Update passed");
                    List<List<ToDo>> tabCollection = new ArrayList<>();
                    for(int i = 0; i < modifiedTabs.size(); i++){
                        List<ToDo> toDos = dao.getToDoList(modifiedTabs.get(i).getTabId());
                        Log.e(TAG, toDos.toString());
                        tabCollection.add(toDos);
                    }

                    for(int i = 0; i < modifiedTabs.size(); i++){

                        int newId = preOrderedTabIds.get(i);

                        // update new Id for All To-Dos belong to the Tab
                        List<ToDo> toDos = tabCollection.get(i);
                        for(ToDo _toDo : toDos){
                            _toDo.setToDoOwnerId(newId);
                        }

                        // update new Id for Tab
                        Tab tab = modifiedTabs.get(i);
                        tab.setTabId(newId);

                        Log.e(TAG, "After modified " + toDos.toString());
                        dao.updateToDoList(toDos.toArray(new ToDo[0]));
                    }

                    Log.e(TAG, "After modified " + modifiedTabs.toString());
                    dao.updateTab(modifiedTabs.toArray(new Tab[0]));
                    break;

                case Edit:
                    Log.e(TAG, "Edit passed");
                    dao.updateTab(modifiedTabs.toArray(new Tab[0]));
            }

            modifiedTabs.clear();
            return null;
        }
    }

    public LiveData<List<Tab>> getmTabList() {
        return mTabList;
    }

    public boolean isDeleted() {
        return isDeleted;
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        if(updateTabIntoDatabase != null){
            updateTabIntoDatabase.cancel(true);
            updateTabIntoDatabase = null;
        }
    }
}
