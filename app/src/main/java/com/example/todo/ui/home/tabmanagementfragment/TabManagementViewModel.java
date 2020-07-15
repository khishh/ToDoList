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
import java.util.Collections;
import java.util.List;

public class TabManagementViewModel extends AndroidViewModel {

    private static final String TAG = "TabManagementViewModel";

    MutableLiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<Void, Void, List<Tab>> retrieveTabFromDatabase;

    private AsyncTask<List<Tab>, Void, List<Tab>> updateTabIntoDatabase;

    public TabManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public void retrieveTabList(){
        retrieveTabFromDatabase = new RetrieveTabFromDatabase();
        retrieveTabFromDatabase.execute();
    }

    public void updateTabList(int fromPos, int toPos){

        Log.d(TAG, "fromPos == " + fromPos + " toPos == " + toPos);

        swapTabOrder(fromPos, toPos);

        List<Tab> tabs = new ArrayList<>();
        tabs.add(mTabList.getValue().get(fromPos));
        tabs.add(mTabList.getValue().get(toPos));
        updateTabIntoDatabase = new UpdateTabIntoDatabase();
        updateTabIntoDatabase.execute(tabs);
    }

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

    private class UpdateTabIntoDatabase extends AsyncTask<List<Tab>, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(List<Tab>... lists) {

//            Log.d(TAG, "UpdateTabIntoDatabase doInBackground started");

            List<Tab> tabList = lists[0];

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            int fromPosId = tabList.get(0).getTabId();
            int toPosId = tabList.get(1).getTabId();

            // get All List<To-Do> with indexes of swapped tabs
            List<ToDo> fromPosToDoList = dao.getToDoList(fromPosId);
            List<ToDo> toPosToDoList = dao.getToDoList(toPosId);

//            Log.d(TAG, "fromPosToDoList Size " + fromPosToDoList.size());
            for(ToDo todo : fromPosToDoList){
                todo.setToDoOwnerId(toPosId);
//                Log.d(TAG, "fromPosToDoList newOwnerId = " + toPosId);
            }

            dao.updateToDoList(fromPosToDoList);

//            Log.d(TAG, "toPosToDoList Size " + toPosToDoList.size());


            for(ToDo todo : toPosToDoList){
                todo.setToDoOwnerId(fromPosId);
//                Log.d(TAG, "toPosToDoList newOwnerId = " + fromPosId);
            }
            dao.updateToDoList(toPosToDoList);


            //update tabIndex
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

    private void retrievedTabList(List<Tab> tabs){
        mTabList.setValue(tabs);
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

}
