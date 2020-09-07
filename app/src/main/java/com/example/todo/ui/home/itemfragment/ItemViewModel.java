package com.example.todo.ui.home.itemfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemViewModel
 *
 * 1. Keep the updated To-Dos belong to a currently displayed Tab inside mDoList, asynchronously receiving
 * new data from database if there is any changes.
 * 2. Execute 3 types of operations as requested from ItemFragment
 *  - insert new To-Dos
 *  - delete existing completed To-Dos (== whose isDone is true)
 *  - update existing To-Do which users changed its content
 *
 */
public class ItemViewModel extends AndroidViewModel {

    /**
     * ActionType enum : 3 types of actions this class can operate
     */
    public enum ActionType{
        Add, Edit, Delete
    }

    private static final String TAG = "ItemViewModel";

    private LiveData<List<ToDo>> mDoList = new MutableLiveData<>();

    private int curTabId;

    // this list holds all To-Do(s) which will be either added/deleted/edited
    private List<ToDo> modifiedToDos = new ArrayList<>();
    private ActionType actionType;

    private AsyncTask<Void, Void, Void> updateToDoIntoDatabase;

    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    public ItemViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ItemViewModel created");
    }

    /**
     * Load all To-Dos belongs to the Tab whose tabId is equal to tabId in parameter
     */
    public void loadToDoList(int tabId){
        curTabId = tabId;
        mDoList = dao.getLiveToDoList(tabId);
    }

    /**
     * Methods to update To-Do table in Database
     * updateToDoIntoDatabase will make 3 different executions depending on the actionType.
     */
    public void updateToDoDataBase() {
        updateToDoIntoDatabase = new UpdateToDoTask();
        updateToDoIntoDatabase.execute();
    }

    /**
     * Create the new To-Do instance and store it in modifiedToDos.
     * Then call updateToDoDataBase method to actually save it into database in background thread.
     */
    public void addNewToDo(String newToDoContent){

        ToDo newToDo = new ToDo(
                curTabId,
                newToDoContent,
                false);

        actionType = ActionType.Add;
        modifiedToDos.add(newToDo);
        updateToDoDataBase();

    }

    /**
     * Update the content of To-Do a user clicked and store it in modifiedToDos.
     * Then call updateToDoDataBase method to actually update it inside database in background thread.
     */
    public void updateToDoContentAtPosition(int position, String newToDoContent){

        ToDo updatedToDo = mDoList.getValue().get(position);
        updatedToDo.setContent(newToDoContent);
        modifiedToDos.add(updatedToDo);
        actionType = ActionType.Edit;
        updateToDoDataBase();

    }

    /**
     * Update isDone value of To-Do a user clicked and store it into modifiedToDos.
     * call updateToDoDataBase method to actually update it inside database in background thread.
     */
    public void updateToDoIsDoneAtPosition(int position, boolean newIsDone){

        ToDo updatedToDo = mDoList.getValue().get(position);
        updatedToDo.setDone(newIsDone);
        modifiedToDos.add(updatedToDo);
        actionType = ActionType.Edit;
        updateToDoDataBase();

    }

    /**
     * Traverse mDoList to gather all To-Dos whose isDone is true, and save them into modifiedToDos.
     * Then call updateToDoDatabase method to delete them inside database in background thread.
     */
    public void removeAllDoneToDo(){

        List<ToDo> curList = mDoList.getValue();

        int i = 0;
        while(i < curList.size()){
            if(curList.get(i).isDone()){
                modifiedToDos.add(curList.get(i));
            }
            i++;
        }

        Log.d(TAG, modifiedToDos.toString());
        actionType = ActionType.Delete;
        updateToDoDataBase();

    }

    /*
    public void removeAllToDo(){
        List<ToDo> curList = mDoList.getValue();
        curList.clear();
        mDoList.setValue(curList);
        isUpdated = true;
        updateToDoList();
    }
     */

    /**
     * AsyncTask class to operate 3 kinds of tasks depending on the current value of ActionType.
     * actionType must be set before calling execute() of this class.
     */
    private class UpdateToDoTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            switch (actionType){

                case Add:
                    Log.e(TAG, "Insert passed");
                    dao.insertToDoList(modifiedToDos.toArray(new ToDo[0]));
                    break;

                case Delete:
                    Log.e(TAG, "Delete passed");
                    dao.deleteToDos(modifiedToDos.toArray(new ToDo[0]));
                    break;

                case Edit:
                    Log.e(TAG, "Update passed");
                    dao.updateToDoList(modifiedToDos.toArray(new ToDo[0]));
            }

            modifiedToDos.clear();
            return null;
        }
    }

    /**
     * Accessors
     */

    public LiveData<List<ToDo>> getmDoList() {
        return mDoList;
    }

    public String getToDoContentAtPosition(int position){
        return mDoList.getValue().get(position).getContent();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "ItemViewModel onCleared");

        if(updateToDoIntoDatabase != null){
            updateToDoIntoDatabase.cancel(true);
            updateToDoIntoDatabase = null;
        }

    }

}
