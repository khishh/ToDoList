package com.example.todo.ui.home.itemmanagementfragment;

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

public class ItemManagementViewModel extends AndroidViewModel {

    private static final String TAG = "ItemManagementViewModel";

    private MutableLiveData<List<ToDo>> mDoList = new MutableLiveData<>();
    private MutableLiveData<Tab> tabs = new MutableLiveData<>();
    private MutableLiveData<Integer> mTabId = new MutableLiveData<>();

    private AsyncTask<Integer, Void, List<ToDo>> retrieveToDoFromDatabase;

    // the instance of UpdateTabIntoDatabase class extends to AsyncTask to save all UPDATED Tabs into Room Database
    private AsyncTask<List<ToDo>, Void, Void> updateToDoList;

    private AsyncTask<List<ToDo>, Void, Void> removeDoneToDo;


    public ItemManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadToDoList(int tabId){
        mTabId.setValue(tabId);
        retrieveToDoFromDatabase = new RetrieveToDoTask();
        retrieveToDoFromDatabase.execute(tabId);
    }

    public void updateToDoList(int fromPos, int toPos){
        ToDo fromToDo = mDoList.getValue().get(fromPos);
        ToDo toToDo = mDoList.getValue().get(toPos);

        swapToDoList(fromToDo, toToDo);

        List<ToDo> tabsUpdated = new ArrayList<>();
        tabsUpdated.add(fromToDo);
        tabsUpdated.add(toToDo);

        updateToDoList = new UpdateToDo();
        updateToDoList.execute(tabsUpdated);
    }

    public void deleteSelectedToDo(){
        List<ToDo> doneToDos = removeAllDoneToDos();

        removeDoneToDo = new RemoveDoneToDo();
        removeDoneToDo.execute(doneToDos);
    }

    private class RetrieveToDoTask extends AsyncTask<Integer, Void, List<ToDo>>{

        @Override
        protected List<ToDo> doInBackground(Integer... integers) {
            int tabId = integers[0];

//            Log.d(TAG, "tabId == " + tabId);
            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();
            List<ToDo> toDoList = dao.getToDoList(tabId);

            return toDoList;
        }

        @Override
        protected void onPostExecute(List<ToDo> toDos) {
            toDoListRetrieved(toDos);
            Log.d(TAG, "data loaded");
            Toast.makeText(getApplication(), "ToDoList retrieved from your database", Toast.LENGTH_SHORT).show();
        }
    }

    private void toDoListRetrieved(List<ToDo> toDos){
        mDoList.setValue(toDos);
    }

    private class UpdateToDo extends AsyncTask<List<ToDo>, Void, Void>{

        @Override
        protected Void doInBackground(List<ToDo>... lists) {

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();
            dao.updateToDoList(lists[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplication(), "ToDoList updated into your database", Toast.LENGTH_SHORT).show();
        }
    }

    private class RemoveDoneToDo extends AsyncTask<List<ToDo>, Void, Void>{

        @Override
        protected Void doInBackground(List<ToDo>... lists) {
            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();
            dao.deleteToDos(lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public void swapToDoList(ToDo fromToDo, ToDo toToDo){
        String contentKeep = fromToDo.getContent();
        boolean isDoneKeep = fromToDo.isDone();

        fromToDo.setDone(toToDo.isDone());
        fromToDo.setContent(toToDo.getContent());

        toToDo.setDone(isDoneKeep);
        toToDo.setContent(contentKeep);
    }

    public List<ToDo> removeAllDoneToDos(){
        List<ToDo> curList = mDoList.getValue();
        List<ToDo> doneList = new ArrayList<>();

//        for(ToDo toDo : curList){
//            if(toDo.isDone()){
//                doneList.add(toDo);
//                curList.remove(toDo);
//            }
//        }
        int i = 0;
        while(i < curList.size()){
            if(curList.get(i).isDone()){
                doneList.add(curList.get(i));
                curList.remove(i);
            }
            else ++i;
        }

        mDoList.setValue(curList);
        return doneList;
    }

    public MutableLiveData<List<ToDo>> getmDoList() {
        return mDoList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if(retrieveToDoFromDatabase != null){
            retrieveToDoFromDatabase.cancel(true);
            retrieveToDoFromDatabase = null;
        }

        if(removeDoneToDo != null){
            removeDoneToDo.cancel(true);
            removeDoneToDo = null;
        }

        if(updateToDoList != null){
            updateToDoList.cancel(true);
            updateToDoList = null;
        }
    }
}
