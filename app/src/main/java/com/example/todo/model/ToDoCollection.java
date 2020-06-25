package com.example.todo.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ToDoCollection {

    private final static  String TAG = "ToDoCollection";

    private List<List<ToDo>> collection = new ArrayList<>();

    private static ToDoCollection instance;

    private ToDoCollection(){
        setUpToDoItems();
    }

    public static ToDoCollection getInstance() {
        if(instance == null){
            instance = new ToDoCollection();
        }

        return instance;
    }

    private void setUpToDoItems(){

        for(int i = 0; i < 10; i++){
            List<ToDo> subCollection = new ArrayList<>();
            Random r = new Random();
            int rInt = r.nextInt(10)+1;
            Log.d(TAG, i + ": " + rInt);
            for(int j = 0; j < rInt; j++){
                subCollection.add(new ToDo(String.valueOf(j+1), false));
            }
            this.collection.add(subCollection);
        }

        Log.d(TAG, this.collection.toString());

    }

    public List<List<ToDo>> getCollection() {
        return collection;
    }
}
