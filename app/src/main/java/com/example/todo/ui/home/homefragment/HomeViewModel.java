package com.example.todo.ui.home.homefragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.R;
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
 *
 */

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private LiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<Tab, Void, Void> insertTabsIntoDatabase;

    private SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplication());
    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    private static final String initialTitle = "How to use";
    private static final int[] initialHowToUse = {
            R.string.instruction_tab_title,
            R.string.instruction_task_example1,
            R.string.instruction_task_example2,
            R.string.instruction_add,
            R.string.instruction_delete,
            R.string.instruction_menu,
            R.string.instruction_item_management,
            R.string.instruction_tab_management
    };

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
            Tab initialTab = setUpToDoItems();
            insertTabsIntoDatabase = new InsertTabsIntoDataBase();
            insertTabsIntoDatabase.execute(initialTab);
        }
    }

    /**
     * Only called when this is the first launch
     * Creating the initial data to be shown in the app
     */
    private Tab setUpToDoItems(){
        List<ToDo> toDos = new ArrayList<>();
        Tab howToUseTab = new Tab(initialTitle);

        for (int resourceId : initialHowToUse) {
            ToDo _toDo;
            if(resourceId != initialHowToUse[2]){
                _toDo = new ToDo(0, getApplication().getResources().getString(resourceId), false);
            }
            else{
                _toDo = new ToDo(0, getApplication().getResources().getString(resourceId), true);
            }

            toDos.add(_toDo);
        }

        howToUseTab.setToDoList(toDos);
        return howToUseTab;
    }

    /**
     * AsyncTask to save initial data set into database
     */
    private class InsertTabsIntoDataBase extends AsyncTask<Tab, Void, Void>{

        @Override
        protected Void doInBackground(Tab... lists) {
            Tab tabList = lists[0];

            dao.insertToDoWithTab(tabList);
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