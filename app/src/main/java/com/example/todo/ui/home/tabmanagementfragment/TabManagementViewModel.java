package com.example.todo.ui.home.tabmanagementfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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

    // *---  fields ---*

    private static final String TAG = "TabManagementViewModel";

    //  store all tabs retrieved from Room Database
    MutableLiveData<List<Tab>> mTabList = new MutableLiveData<>();

    //  the instance of RetrieveTabFromDatabase class extends to AsyncTask to get All Tabs from Room Database
    private AsyncTask<Void, Void, List<Tab>> retrieveTabFromDatabase;

    // the instance of UpdateTabIntoDatabase class extends to AsyncTask to save all UPDATED Tabs into Room Database
    private AsyncTask<List<Tab>, Void, List<Tab>> updateTabIntoDatabase;

    private AsyncTask<Tab, Void, Tab> deleteTabFromDatabase;

    private AsyncTask<Tab, Void, List<Tab>> addNewTabIntoDatabase;

    // default constructor
    public TabManagementViewModel(@NonNull Application application) {
        super(application);
    }

    // retrieving all Tabs in Room database by calling execute()
    public void retrieveTabList(){
        retrieveTabFromDatabase = new RetrieveTabFromDatabase();
        retrieveTabFromDatabase.execute();
    }

    // When users sort Tab from screen, Tab stored in Room will be also sorted accordingly.
    // - fromPos -> the position of Tab in RecyclerView where users started dragging from
    // - toPos   -> the position of Tab in RecyclerView where users sending dragging at
    public void updateTabList(int fromPos, int toPos){

        // Log.d(TAG, "fromPos == " + fromPos + " toPos == " + toPos);

        // update the order of mTabList based on fromPos and toPos
        swapTabOrder(fromPos, toPos);

        // create ArrayList to store two sorted Tab
        List<Tab> tabs = new ArrayList<>();
        tabs.add(mTabList.getValue().get(fromPos));
        tabs.add(mTabList.getValue().get(toPos));

        updateTabIntoDatabase = new UpdateTabIntoDatabase();
        updateTabIntoDatabase.execute(tabs);
    }

    public void deleteTab(int deletePos){

        Tab deleteTab = deleteTabAtPosition(deletePos);

        deleteTabFromDatabase = new DeleteTabFromDataBase();
        deleteTabFromDatabase.execute(deleteTab);

    }

    public void addNewTab(String newTabTitle){

        Tab newTab = new Tab(mTabList.getValue().size(), newTabTitle);

        addNewTabIntoDatabase = new AddNewTabIntoDatabase();
        addNewTabIntoDatabase.execute(newTab);
    }

    // RetrieveTabFromDataBase class
    // gets all Tabs stored in Room in doInBackground()
    // call setValue() method on mTabList to assign retrieved List<Tab> after finish retrieval
    private class RetrieveTabFromDatabase extends AsyncTask<Void, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(Void... voids) {
            return TabToDoDataBase.getInstance(getApplication()).tabToDoDao().getAllTab();
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            retrievedTabList(tabs);
//            Toast.makeText(getApplication(), "TabList retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }

    // UpdateTabIntoDatabase class
    // updates given Tab(s) in Room database in doInBackground()
    private class UpdateTabIntoDatabase extends AsyncTask<List<Tab>, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(List<Tab>... lists) {

//            Log.d(TAG, "UpdateTabIntoDatabase doInBackground started");

            // get Tab(s) to be updated
            List<Tab> tabList = lists[0];

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            // get the original tabIds from two Tabs before sorting
            int fromPosId = tabList.get(0).getTabId();
            int toPosId = tabList.get(1).getTabId();

            // get All List<To-Do> with tabIds of two Tabs before sorting
            List<ToDo> fromPosToDoList = dao.getToDoList(fromPosId);
            List<ToDo> toPosToDoList = dao.getToDoList(toPosId);

            // change each To-Do's ownerId to toPosId and call updateToDoList() to reflect the changes
//            Log.d(TAG, "fromPosToDoList Size " + fromPosToDoList.size());
            for(ToDo todo : fromPosToDoList){
                todo.setToDoOwnerId(toPosId);
//                Log.d(TAG, "fromPosToDoList newOwnerId = " + toPosId);
            }

            dao.updateToDoList(fromPosToDoList);

//            Log.d(TAG, "toPosToDoList Size " + toPosToDoList.size());

            // change each To-Do's ownerId to toPosId and call updateToDoList() to reflect the changes
            for(ToDo todo : toPosToDoList){
                todo.setToDoOwnerId(fromPosId);
//                Log.d(TAG, "toPosToDoList newOwnerId = " + fromPosId);
            }
            dao.updateToDoList(toPosToDoList);


            //update tabId of two Tabs
            int tabIndexKeep = fromPosId;

            Log.d(TAG, "Before = 0's title = " + tabList.get(0).getTabTitle() + " 1's title " + tabList.get(1).getTabTitle());
            Log.d(TAG, "Before = 0's tabId = " + (tabList.get(0).getTabId()) + " 1's tabId " + (tabList.get(1).getTabId()));
            String tabTitleKeep = tabList.get(0).getTabTitle();
            tabList.get(0).setTabId(toPosId);
            tabList.get(1).setTabId(tabIndexKeep);
            Log.d(TAG, "After = 0's title = " + tabList.get(0).getTabTitle() + " 1's title " + tabList.get(1).getTabTitle());
            Log.d(TAG, "After = 0's tabId = " + tabList.get(0).getTabId() + " 1's tabId " + tabList.get(1).getTabId());

            dao.updateTab(tabList.get(0));
            dao.updateTab(tabList.get(1));

            return tabList;
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            Toast.makeText(getApplication(), "updated TabList", Toast.LENGTH_SHORT).show();
        }
    }

    private class DeleteTabFromDataBase extends AsyncTask<Tab, Void, Tab>{

        @Override
        protected Tab doInBackground(Tab... tabs) {

            Log.d(TAG, "DELETE started");
            Tab deleteTab = tabs[0];

            int deleteTabId = deleteTab.getTabId();

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            dao.deleteTab(deleteTab);
            dao.deleteAllToDoOfId(deleteTabId);

            return deleteTab;
        }

        @Override
        protected void onPostExecute(Tab tab) {
            deletedTab(tab);
        }
    }

    private class AddNewTabIntoDatabase extends AsyncTask<Tab, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(Tab... tabs) {
            Tab newTab = tabs[0];

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            dao.insertTab(newTab);

            return dao.getAllTab();
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            retrievedTabList(tabs);
            Log.d(TAG, tabs.toString());
        }
    }

    private void retrievedTabList(List<Tab> tabs){
        mTabList.setValue(tabs);
    }

    private void deletedTab(Tab tab){
        List<Tab> cur = mTabList.getValue();
        cur.remove(tab);
        mTabList.setValue(cur);
    }



    public MutableLiveData<List<Tab>> getmTabList() {
        return mTabList;
    }

    public void swapTabOrder(int fromTabIndex, int toTabIndex){

        Tab fromTab = mTabList.getValue().get(fromTabIndex);
        Tab toTab = mTabList.getValue().get(toTabIndex);

        mTabList.getValue().set(toTabIndex, fromTab);
        mTabList.getValue().set(fromTabIndex, toTab);
    }

    public Tab deleteTabAtPosition(int deleteTabIndex){

        Tab deleteTab = mTabList.getValue().get(deleteTabIndex);

        Log.d(TAG, "Delete Tab == " + deleteTab.getTabTitle());
        mTabList.getValue().remove(deleteTab);

        return deleteTab;
    }

}
