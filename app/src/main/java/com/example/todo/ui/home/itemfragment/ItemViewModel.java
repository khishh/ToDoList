package com.example.todo.ui.home.itemfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    private static final String TAG = "ItemViewModel";
    private MutableLiveData<List<ToDo>> mDoList = new MutableLiveData<>();
    private MutableLiveData<Integer> mTabId = new MutableLiveData<>();

    private AsyncTask<Integer, Void, List<ToDo>> retrieveToDoFromDatabase;

    private AsyncTask<Integer, Void, Void> updateToDoIntoDatabase;

    private boolean isUpdated = false;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ItemViewModel created");
    }

    public void loadToDoList(int tabId){

        mTabId.setValue(tabId);

        retrieveToDoFromDatabase = new RetrieveToDoTask();
        retrieveToDoFromDatabase.execute(tabId);
    }

    private class RetrieveToDoTask extends AsyncTask<Integer, Void, List<ToDo>>{

        @Override
        protected List<ToDo> doInBackground(Integer... integers) {
            int tabId = integers[0];

//            Log.d(TAG, "tabId == " + tabId);

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();
            List<ToDo> toDoList = dao.getToDoList(tabId);

//            Log.d(TAG, toDoList.size() + " " + toDoList.toString());

            return toDoList;
        }

        @Override
        protected void onPostExecute(List<ToDo> toDos) {
            toDoListRetrieved(toDos);
//            Log.d(TAG, toDos.toString());
            Log.d(TAG, "data loaded");
            Toast.makeText(getApplication(), "ToDoList retrieved from your database", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateToDoList(){
        updateToDoIntoDatabase = new UpdateToDoTask();
        updateToDoIntoDatabase.execute(mTabId.getValue());
    }

    private class UpdateToDoTask extends AsyncTask<Integer, Void, Void>{


        @Override
        protected Void doInBackground(Integer... integers) {
            int tabId = integers[0];

//            Log.d(TAG, "UPDATE tabId == " + tabId);

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

            dao.deleteAllToDoOfId(tabId);
            dao.insertToDoList(mDoList.getValue());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplication(), "Changed Info saved into database", Toast.LENGTH_SHORT);
            this.cancel(true);
            updateToDoIntoDatabase = null;
        }
    }

    private void toDoListRetrieved(List<ToDo> toDos){
        mDoList.setValue(toDos);
    }



    public String getToDoContentAtPosition(int position){
        return mDoList.getValue().get(position).getContent();
    }

    public void addNewToDo(String newToDoContent){
        List<ToDo> curList = mDoList.getValue();
        curList.add(new ToDo(
                mTabId.getValue(),
                newToDoContent,
                false
        ));
        mDoList.setValue(curList);
        isUpdated = true;
        updateToDoList();
    }

    public void updateToDoContentAtPosition(int position, String newToDoContent){
        List<ToDo> curList = mDoList.getValue();
        curList.set(position, new ToDo(
                mTabId.getValue(),
                newToDoContent,
                false
        ));
        mDoList.setValue(curList);
        isUpdated = true;
        updateToDoList();
    }

    public void removeAllDoneToDo(){
        List<ToDo> curList = mDoList.getValue();

        int i = 0;
        while(i < curList.size()){
            if(curList.get(i).isDone()){
                curList.remove(i);
            }
            else ++i;
        }

        mDoList.setValue(curList);
        isUpdated = true;
        updateToDoList();
    }

    public void removeAllToDo(){
        List<ToDo> curList = mDoList.getValue();
        curList.clear();
        mDoList.setValue(curList);
        isUpdated = true;
        updateToDoList();
    }

    // getter and setter

    public MutableLiveData<List<ToDo>> getMDoList() {
        return mDoList;
    }

    public MutableLiveData<Integer> getmTabId() {
        return mTabId;
    }

    public void setMTabId(int tabId){
        mTabId.setValue(tabId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "ItemViewModel onCleared");

        if(isUpdated){
            updateToDoList();
        }

        if(retrieveToDoFromDatabase != null){
            retrieveToDoFromDatabase.cancel(true);
            retrieveToDoFromDatabase = null;
        }

        if(updateToDoIntoDatabase != null){
            updateToDoIntoDatabase.cancel(true);
            updateToDoIntoDatabase = null;
        }

    }

}
