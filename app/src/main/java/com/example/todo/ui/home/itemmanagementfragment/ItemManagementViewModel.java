package com.example.todo.ui.home.itemmanagementfragment;

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

import java.util.ArrayList;
import java.util.List;

public class ItemManagementViewModel extends AndroidViewModel {

    public enum ActionType{
        Move, Update, Delete, Order
    }

    private static final String TAG = "ItemManagementViewModel";

    private LiveData<List<ToDo>> mDoList = new MutableLiveData<>();
    private LiveData<List<Tab>> tabs = new MutableLiveData<>();

    private int curTabId;
    private List<ToDo> selectedToDos = new ArrayList<>();
    private ActionType actionType;

    TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    // the instance of UpdateTabIntoDatabase class extends to AsyncTask to save all UPDATED Tabs into Room Database
    private AsyncTask<Void, Void, Void> updateToDoIntoDatabase;

    public ItemManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadToDoList(int tabId){
        curTabId = tabId;
        mDoList = dao.getLiveToDoList(tabId);

    }

    public void loadTabs(){
        tabs = dao.getAllLiveTab();
    }

    public void updateToDoList() {
        updateToDoIntoDatabase = new UpdateToDoTask();
        updateToDoIntoDatabase.execute();
    }

    /*
    public void swapToDos(int fromPos, int toPos){
        ToDo fromToDo = mDoList.getValue().get(fromPos);
        ToDo toToDo = mDoList.getValue().get(toPos);

        swap(fromToDo, toToDo);

        selectedToDos.add(fromToDo);
        selectedToDos.add(toToDo);

        actionType = ActionType.Update;
        updateToDoList();
    }

    private void swap(ToDo fromToDo, ToDo toToDo){
        String contentKeep = fromToDo.getContent();
        boolean isDoneKeep = fromToDo.isDone();

        fromToDo.setDone(toToDo.isDone());
        fromToDo.setContent(toToDo.getContent());

        toToDo.setDone(isDoneKeep);
        toToDo.setContent(contentKeep);
    }

     */

    public void deleteSelectedToDo(List<ToDo> curList){
        int i = 0;
        while(i < curList.size()){
            if(curList.get(i).isSelected()){
                selectedToDos.add(curList.get(i));
            }
            i++;
        }
        Log.e(TAG, selectedToDos.toString());
        actionType = ActionType.Delete;
        updateToDoList();
    }

    public void saveLastToDoOrder(List<ToDo> orderedList){
        Log.e(TAG, "orderedList " + orderedList.toString());
        selectedToDos.addAll(orderedList);
        actionType = ActionType.Update;
        updateToDoList();
    }

    public void moveToDoToOtherTab(int targetTabId, List<ToDo> curList){
        int i = 0;
        while(i < curList.size()){
            ToDo todo = curList.get(i);
            if(todo.isSelected()) {
                todo.setToDoOwnerId(targetTabId);
                selectedToDos.add(todo);
            }
            i++;
        }

        actionType = ActionType.Move;
        updateToDoList();
    }

    private class UpdateToDoTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            List<ToDo> test = dao.getToDoList(curTabId);
            Log.e(TAG, "Before: " + test.toString());

            Log.e(TAG, "selectedList " + selectedToDos.toString());

            switch (actionType){

                case Update:
                    Log.e(TAG, "Update passed");
                    dao.updateToDoList(selectedToDos.toArray(new ToDo[0]));
                    break;

                case Move:
                    Log.e(TAG, "Insert passed");
                    dao.updateToDoList(selectedToDos.toArray(new ToDo[0]));
                    break;

                case Delete:
                    Log.e(TAG, "Delete passed");
                    dao.deleteToDos(selectedToDos.toArray(new ToDo[0]));
                    break;

                case Order:
                    Log.e(TAG, "order passed");
                    dao.updateToDoList(selectedToDos.toArray(new ToDo[0]));

                // test

            }

            List<ToDo> test2 = dao.getToDoList(curTabId);
            Log.e(TAG, "After: " + test2.toString());


            selectedToDos.clear();
            return null;
        }
    }


    public LiveData<List<ToDo>> getmDoList() {
        return mDoList;
    }

    public LiveData<List<Tab>> getTabs() {
        return tabs;
    }

    public List<Tab> getTabsValue(){
        return tabs.getValue();
    }

    public boolean isToDoSelected(List<ToDo> curList){

        boolean isAtLeastOneSelected = false;
        int count = 0;

        while(!isAtLeastOneSelected && count < curList.size()){
            if(curList.get(count).isSelected()){
                isAtLeastOneSelected = true;
            }
            count++;
        }

        return isAtLeastOneSelected;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
