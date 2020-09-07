package com.example.todo.ui.home.homefragment;

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
import com.example.todo.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * HomeViewModel
 *
 * 1. Keep the most recent Tab data into LiveData<List<Tab>> mTabList loaded from Database
 * 2. If first launch of this app, save the initial data set into Database
 */

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private LiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<List<Tab>, Void, Void> insertTabsIntoDatabase;

    private SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "HomeViewModel created");
    }

    /**
     * Load all Tabs from database
     * If this is the first launch, save the initial data into Database and display it.
     */
    public void loadTabs(){

        long updateTime = sharedPreferencesHelper.getUpdateTime();
        mTabList = dao.getAllLiveTab();

        // case if this ist he first launch
        if(updateTime == 0){
            // case if there is no data loaded into database yet
            List<Tab> tabList = setUpToDoItems();
            insertTabsIntoDatabase = new InsertTabsIntoDataBase();
            insertTabsIntoDatabase.execute(tabList);
        }
    }

    /**
     * Only called when this is the first launch
     * Creating the initial data to be shown in the app
     */
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

            subCollection.add(new ToDo(rInt, "TAB " + (i+1), false));
            tab.setToDoList(subCollection);

            tabList.add(tab);
        }
        return tabList;
    }

    /**
     * AsyncTask to save initial data set into database
     */
    private class InsertTabsIntoDataBase extends AsyncTask<List<Tab>, Void, Void>{

        @Override
        protected Void doInBackground(List<Tab>... lists) {
            List<Tab> tabList = lists[0];

            for (int i = 0; i < tabList.size(); i++){
                dao.insertToDoWithTab(tabList.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // after save Tabs into database, record the current time
            sharedPreferencesHelper.saveUpdateTime(System.nanoTime());
        }
    }

    /**
     * Accessors
     */
    public LiveData<List<Tab>> getmTabList() {
        return mTabList;
    }

    public int getTabIdAtPosition(int position){
        return mTabList.getValue().get(position).getTabId();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "HomeViewModel onCleared");

        // cancel AsyncTask and set it to null in order to prevent memory leaks
        if(insertTabsIntoDatabase != null){
            insertTabsIntoDatabase.cancel(true);
            insertTabsIntoDatabase = null;
        }
    }
}