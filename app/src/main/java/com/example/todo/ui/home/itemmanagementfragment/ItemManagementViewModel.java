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

/**
 * ItemManagementViewModel
 *
 * 1. Keeps all To-Dos of the last visited Tab and all Tabs, used for users to select one Tab
 *    in MoveToDoDialog to decide the destination of selected To-Dos.
 *
 * 2. Executes 3 types of operations interacting with database
 *  - Move   :move all selected To-Dos to other Tab selected in MoveToDoDialog and update them inside database
 *  - Delete : delete all selected To-Dos from database
 *  - Order  : update all To-Dos into database in a way to keep new ordering
 *
 */

public class ItemManagementViewModel extends AndroidViewModel {

    /**
     * ActionType enum : 3 types of actions this class can operate
     */
    public enum ActionType{
        Move, Delete, Order
    }

    private static final String TAG = ItemManagementFragment.class.getSimpleName();

    private LiveData<List<ToDo>> mDoList = new MutableLiveData<>();
    private LiveData<List<Tab>> tabs = new MutableLiveData<>();

    private List<ToDo> selectedToDos = new ArrayList<>();
    private ActionType actionType;

    TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();

    // the instance of UpdateTabIntoDatabase class extends to AsyncTask to save all updated Tabs into Room Database
    private AsyncTask<Void, Void, Void> updateToDoIntoDatabase;

    public ItemManagementViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadToDoList(int tabId){
        mDoList = dao.getLiveToDoList(tabId);
    }

    public void loadTabs(){
        tabs = dao.getAllLiveTab();
    }

    /**
     * Methods to update To-Do table in Database
     * updateToDoIntoDatabase can make 3 different executions depending on the actionType.
     */
    public void updateToDoList() {
        updateToDoIntoDatabase = new ToDoDatabaseTask();
        updateToDoIntoDatabase.execute();
    }

    /**
     * Method to store all selected To-Dos inside selectedToDos.
     * As actionType is set to Move, when we call updateToDoList(), UpdateToDoTask AsyncTask
     * will update To-Dos with a new owner Tab whose tabId is equal to targetTabId inside database,
     */
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

    /**
     * Method to store all selected To-Dos inside selectedToDos.
     * As actionType is set to Delete, when we call updateToDoList(), ToDoDatabaseTask AsyncTask
     * will delete all To-Dos inside selectedToDos inside database.
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

    /**
     * Method to store all selected To-Dos inside selectedToDos.
     * As actionType is set to Order, when we call updateToDoList(), UpdateToDoTask AsyncTask
     * will update To-Dos so that their new order will be preserved.
     */
    public void updateToDoOrder(List<ToDo> orderedList){
        Log.e(TAG, "orderedList " + orderedList.toString());
        selectedToDos.addAll(orderedList);
        actionType = ActionType.Order;
        updateToDoList();
    }

    /**
     * AsyncTask class to operate 3 kinds of tasks depending on the current value of ActionType.
     * actionType must be set before calling execute() of this class.
     */
    private class ToDoDatabaseTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

//            List<ToDo> test = dao.getToDoList(curTabId);
//            Log.e(TAG, "Before: " + test.toString());
//
//            Log.e(TAG, "selectedList " + selectedToDos.toString());

            switch (actionType){

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

            }

//            // test
//            List<ToDo> test2 = dao.getToDoList(curTabId);
//            Log.e(TAG, "After: " + test2.toString());

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

    /**
     * check if at least one To-Do is selected
     */
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

        if(updateToDoIntoDatabase != null){
            updateToDoIntoDatabase.cancel(true);
            updateToDoIntoDatabase = null;
        }
    }
}
