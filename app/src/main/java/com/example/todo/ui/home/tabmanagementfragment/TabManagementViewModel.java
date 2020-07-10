package com.example.todo.ui.home.tabmanagementfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.Tab;
import com.example.todo.model.TabToDoDataBase;

import java.util.List;

public class TabManagementViewModel extends AndroidViewModel {

    MutableLiveData<List<Tab>> mTabList = new MutableLiveData<>();

    private AsyncTask<Void, Void, List<Tab>> retrieveTabFromDatabase;

    public TabManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public void updateTabList(){
        retrieveTabFromDatabase = new RetrieveTabFromDatabase();
        retrieveTabFromDatabase.execute();
    }

    private class RetrieveTabFromDatabase extends AsyncTask<Void, Void, List<Tab>>{

        @Override
        protected List<Tab> doInBackground(Void... voids) {
            return TabToDoDataBase.getInstance(getApplication()).tabToDoDao().getAllTab();
        }

        @Override
        protected void onPostExecute(List<Tab> tabs) {
            retrievedTabList(tabs);
            Toast.makeText(getApplication(), "Tablist retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrievedTabList(List<Tab> tabs){
        mTabList.setValue(tabs);
    }

    public MutableLiveData<List<Tab>> getmTabList() {
        return mTabList;
    }


}
