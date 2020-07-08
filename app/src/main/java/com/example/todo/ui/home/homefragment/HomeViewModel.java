package com.example.todo.ui.home.homefragment;

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
import com.example.todo.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    MutableLiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<List<Tab>, Void, List<Tab>> insertTabsIntoDatabase;

    private AsyncTask<Void, Void, List<Tab>> retrieveTabsFromDatabase;

    private SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplication());

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void initializeData(){

        long updateTime = sharedPreferencesHelper.getUpdateTime();

        if(updateTime == 0){
            // case if there is no data loaded into database yet
            List<Tab> tabList = setUpToDoItems();
            insertTabsIntoDatabase = new InsertTabsIntoDataBase();
            insertTabsIntoDatabase.execute(tabList);
        }
        else{
            retrieveTabsFromDatabase = new RetrieveTabsFromDataBase();
            retrieveTabsFromDatabase.execute();
        }

    }

    private List<Tab> setUpToDoItems(){

        List<Tab> tabList = new ArrayList<>();

        for(int i = 0; i < 10; i++){

            Tab tab = new Tab(i, "Tab" + (i+1));
            List<ToDo> subCollection = new ArrayList<>();
            Random r = new Random();
            int rInt = r.nextInt(20)+1;

            for(int j = 0; j < rInt; j++){
                if(j % 2 == 0)
                    subCollection.add(new ToDo(i, String.valueOf(j+1), false));
                else
                    subCollection.add(new ToDo(i, String.valueOf(j+1), true));
            }
            tab.setToDoList(subCollection);

            tabList.add(tab);
        }

        Log.d(TAG, tabList.toString());

        return tabList;
    }

    private class InsertTabsIntoDataBase extends AsyncTask<List<Tab>, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(List<Tab>... lists) {

            List<Tab> tabList = lists[0];

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            for (int i = 0; i < tabList.size(); i++){
                dao.insertToDoWithTab(tabList.get(i));
            }

            // after save dogs into database, record the current time
            sharedPreferencesHelper.saveUpdateTime(System.nanoTime());

            return tabList;
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            tabsRetrieved(tabs);
            Log.d(TAG, tabs.toString());
            Log.d(TAG, "data saved");
            Toast.makeText(getApplication(), "Data saved into your local data", Toast.LENGTH_LONG).show();
        }
    }

    private class RetrieveTabsFromDataBase extends AsyncTask<Void, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(Void... voids) {
            return TabToDoDataBase.getInstance(getApplication()).tabToDoDao().getAllTab();
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            tabsRetrieved(tabs);
            Toast.makeText(getApplication(), "Data retrieved from your local data", Toast.LENGTH_LONG).show();
        }
    }

    private void tabsRetrieved(List<Tab> tabs) {
        mTabList.setValue(tabs);
    }


    // getter and setter

    public MutableLiveData<List<Tab>> getTabList() {
        return mTabList;
    }

    public String getTabTitleWithPosition(int position){
        return mTabList.getValue().get(position).getTabTitle();
    }

    public int getTabListSize(){
        return mTabList.getValue().size();
    }

    public int getTabIdAtPosition(int position){
        return mTabList.getValue().get(position).getTabIndex();
    }
}