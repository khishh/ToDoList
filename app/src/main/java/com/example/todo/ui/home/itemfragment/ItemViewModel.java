package com.example.todo.ui.home.itemfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    public enum ActionType{
        Insert, Update, Delete
    }

    private static final String TAG = "ItemViewModel";
    private LiveData<List<ToDo>> mDoList = new MutableLiveData<>();

    private int curTabId ;

    private AsyncTask<Void, Void, Void> updateToDoIntoDatabase;

    private TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    private List<ToDo> modifiedToDos = new ArrayList<>();
    private ActionType actionType;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ItemViewModel created");
    }

    public void loadToDoList(int tabId){
        curTabId = tabId;
        mDoList = dao.getLiveToDoList(tabId);
    }

    public void updateToDoList() {
        updateToDoIntoDatabase = new UpdateToDoTask();
        updateToDoIntoDatabase.execute();
    }

    public void addNewToDo(String newToDoContent){

        ToDo newToDo = new ToDo(
                curTabId,
                newToDoContent,
                false);

        actionType = ActionType.Insert;
        modifiedToDos.add(newToDo);
        updateToDoList();

    }

    public void updateToDoContentAtPosition(int position, String newToDoContent){

        ToDo updatedToDo = mDoList.getValue().get(position);
        updatedToDo.setContent(newToDoContent);
        modifiedToDos.add(updatedToDo);
        actionType = ActionType.Update;
        updateToDoList();

    }

    public void updateToDoIsDoneAtPosition(int position, boolean newIsDone){

        ToDo updatedToDo = mDoList.getValue().get(position);

        Log.e(TAG, updatedToDo.toString());
        updatedToDo.setDone(newIsDone);
        Log.e(TAG, updatedToDo.toString());
        modifiedToDos.add(updatedToDo);
        actionType = ActionType.Update;
        updateToDoList();

    }

    public void removeAllDoneToDo(){

        List<ToDo> curList = mDoList.getValue();
        Log.d(TAG, curList.toString());

        int i = 0;
        while(i < curList.size()){
            if(curList.get(i).isDone()){
                modifiedToDos.add(curList.get(i));
            }
            i++;
        }

        Log.d(TAG, modifiedToDos.toString());
        actionType = ActionType.Delete;
        updateToDoList();

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

    private class UpdateToDoTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            switch (actionType){

                case Insert:
                    Log.e(TAG, "Insert passed");
                    dao.insertToDoList(modifiedToDos.toArray(new ToDo[0]));
                    break;

                case Delete:
                    Log.e(TAG, "Delete passed");
                    dao.deleteToDos(modifiedToDos.toArray(new ToDo[0]));
                    break;

                case Update:
                    Log.e(TAG, "Update passed");
                    dao.updateToDoList(modifiedToDos.toArray(new ToDo[0]));
                    // test
                    List<ToDo> test = dao.getToDoList(curTabId);
                    Log.e(TAG, test.toString());

            }

            modifiedToDos.clear();
            return null;
        }
    }

    // getter and setter
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
