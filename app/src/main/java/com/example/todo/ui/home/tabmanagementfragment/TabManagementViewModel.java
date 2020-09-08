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


/**
 * TabManagementViewModel
 *
 * 1. Keeps all updated Tabs continuously fetched from database as data changes.
 *
 * 2. Executes 4 types of operations interacting with database
 *  - Add    : Add a newly created Tab into database
 *  - Sort   : Update all Tabs to keep the new order of them
 *  - Delete : Delete a selected Tab from database
 *  - Edit   : Update a existing Tab in database with new tabTitle
 *
 */

public class TabManagementViewModel extends AndroidViewModel {

    private static final String TAG = TabManagementViewModel.class.getSimpleName();

    /**
     * ActionType enum : 4 types of actions this class can operate
     */
    public enum ActionType{
        Add, Sort, Delete, Edit
    }

    LiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<Void, Void, Void> updateTabIntoDatabase;

    private ActionType actionType;

    /**
     * the boolean value to check if Tabs are reordered or any Tab is deleted
     * I have this value because when a user comes back to HomeFragment with the last displayed Tab and its To-Dos,
     * the Tab might no longer exist (delete case) or not be the same Tab anymore (edited or sort case).
     * This value will decide whether HomeFragment has to be refreshed or not when a user clicks on Back or Close button in TabManagementFragment.
     */
    private boolean isDeleterOrSortedOrEditedOrAdded = false;

    /**
     * A list of Tab needs to go through Add or Sort or Delete or Edit.
     */
    private List<Tab> modifiedTabs = new ArrayList<>();

    /**
     * Keep the precious order of TabIds, used for ActionType.Sort
     */
    private List<Integer> preOrderedTabIds = new ArrayList<>();

    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    // default constructor
    public TabManagementViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Retrieve all Tabs from database
     */
    public void retrieveTabList(){
        mTabList = dao.getAllLiveTab();
    }

    /**
     * Methods to update Tab table in Database
     * updateTabIntoDatabase can make 4 different executions depending on the actionType.
     */
    public void updateTabList(){
        updateTabIntoDatabase = new TabDatabaseTask();
        updateTabIntoDatabase.execute();
    }

    /**
     * Method to store a selected Tab inside modifiedTabs.
     * As actionType is set to Delete, when we call updateTabList(), UpdateToDoTask AsyncTask
     * will delete the selected Tab inside database.
     */
    public void deleteTab(Tab deleteTab){
        if(!isDeleterOrSortedOrEditedOrAdded){
            isDeleterOrSortedOrEditedOrAdded = true;
        }

        modifiedTabs.add(deleteTab);
        actionType = ActionType.Delete;
        updateTabList();
    }

    /**
     * Method to store a newly created Tab inside modifiedTabs.
     * As actionType is set to Add, when we call updateTabList(), UpdateToDoTask AsyncTask
     * will insert the new Tab inside database.
     */
    public void addNewTab(String newTabTitle){

        if(!isDeleterOrSortedOrEditedOrAdded){
            isDeleterOrSortedOrEditedOrAdded = true;
        }

        Tab newTab = new Tab(newTabTitle);
        modifiedTabs.add(newTab);
        actionType = ActionType.Add;
        updateTabList();
    }

    /**
     * Method to store all newly ordered Tabs inside modifiedTabs.
     * As actionType is set to Sort, when we call updateTabList(), UpdateToDoTask AsyncTask
     * will update all Tabs in database to keep the new ordering of Tabs.
     */
    public void saveTabOrder(List<Tab> orderedTabList){

        if(!isDeleterOrSortedOrEditedOrAdded){
            isDeleterOrSortedOrEditedOrAdded = true;
        }

        // store the original Id's order
        for(Tab tab : mTabList.getValue()){
            preOrderedTabIds.add(tab.getTabId());
        }

        modifiedTabs.addAll(orderedTabList);
        actionType = ActionType.Sort;
        updateTabList();
    }

    /**
     * Method to store a tab-title edited Tab inside modifiedTabs.
     * As actionType is set to Edit, when we call updateTabList(), TabDatabaseTask AsyncTask
     * will update edited Tab in database.
     */
    public void editTabName(int positionToEdit, String newTabTitle){

        if(!isDeleterOrSortedOrEditedOrAdded){
            isDeleterOrSortedOrEditedOrAdded = true;
        }

        Tab editTab = mTabList.getValue().get(positionToEdit);
        editTab.setTabTitle(newTabTitle);
        modifiedTabs.add(editTab);

        actionType = ActionType.Edit;
        updateTabList();
    }

    /**
     * AsyncTask class to operate 4 kinds of tasks depending on the current value of ActionType.
     * actionType must be set before calling execute() of this class.
     */
    private class TabDatabaseTask extends AsyncTask<Void, Void, Void>{

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

                case Sort:
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

    public boolean isDeleterOrSortedOrEditedOrAdded() {
        return isDeleterOrSortedOrEditedOrAdded;
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
