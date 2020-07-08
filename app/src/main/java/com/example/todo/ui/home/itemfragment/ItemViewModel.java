package com.example.todo.ui.home.itemfragment;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todo.model.Tab;
import com.example.todo.model.TabToDoDao;
import com.example.todo.model.TabToDoDataBase;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends AndroidViewModel {

    private static final String TAG = "ItemViewModel";
    private MutableLiveData<List<ToDo>> mDoList = new MutableLiveData<>();
    private MutableLiveData<Integer> mTabIndex = new MutableLiveData<>();

    private AsyncTask<Integer, Void, List<ToDo>> retrieveToDoFromDatabase;

    public ItemViewModel(@NonNull Application application) {
        super(application);
    }


    public void loadToDoList(int tabIndex){
        mTabIndex.setValue(tabIndex);

        retrieveToDoFromDatabase = new RetrieveToDoTask();
        retrieveToDoFromDatabase.execute(tabIndex);
    }


    private class RetrieveToDoTask extends AsyncTask<Integer, Void, List<ToDo>>{


        @Override
        protected List<ToDo> doInBackground(Integer... integers) {
            int tabIndex = integers[0];

            Log.d(TAG, "tabIndex == " + tabIndex);

            TabToDoDao dao = TabToDoDataBase.getInstance(getApplication()).tabToDoDao();
            List<ToDo> toDoList = dao.getToDoList(tabIndex);

            Log.d(TAG, toDoList.size() + " " + toDoList.toString());

            return toDoList;
        }

        @Override
        protected void onPostExecute(List<ToDo> toDos) {
            toDoListRetrieved(toDos);
            Log.d(TAG, toDos.toString());
            Log.d(TAG, "data loaded");
            Toast.makeText(getApplication(), "ToDoList retrieved from your database", Toast.LENGTH_SHORT).show();
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
                mTabIndex.getValue(),
                newToDoContent,
                false
        ));
        mDoList.setValue(curList);
    }

    public void updateToDoContentAtPosition(int position, String newToDoContent){
        List<ToDo> curList = mDoList.getValue();
        curList.set(position, new ToDo(
                mTabIndex.getValue(),
                newToDoContent,
                false
        ));
        mDoList.setValue(curList);
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
    }

    public void removeAllToDo(){
        List<ToDo> curList = mDoList.getValue();
        curList.clear();
        mDoList.setValue(curList);
    }

    // getter and setter

    public MutableLiveData<List<ToDo>> getmDoList() {
        return mDoList;
    }
}
