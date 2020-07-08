//package com.example.todo.model;
//
//import android.util.Log;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class ToDoCollection {
//
//    private final static  String TAG = "ToDoCollection";
//
//    private List<List<ToDo>> collection = new ArrayList<>();
//
//    private List<String> collectionTitles = new ArrayList<>();
//
//    private static ToDoCollection instance;
//
//    private ToDoCollection(){
//        setUpToDoItems();
//        setUpTabTitles();
//    }
//
//    public static ToDoCollection getInstance() {
//        if(instance == null){
//            instance = new ToDoCollection();
//        }
//
//        return instance;
//    }
//
//    private void setUpToDoItems(){
//
//        for(int i = 0; i < 10; i++){
//            List<ToDo> subCollection = new ArrayList<>();
//            Random r = new Random();
//            int rInt = r.nextInt(20)+1;
////            Log.d(TAG, i + ": " + rInt);
//            for(int j = 0; j < rInt; j++){
//                if(j % 2 == 0)
//                    subCollection.add(new ToDo(String.valueOf(j+1), false));
//                else
//                    subCollection.add(new ToDo(String.valueOf(j+1), true));
//            }
//            this.collection.add(subCollection);
//        }
//
////        Log.d(TAG, this.collection.toString());
//
//    }
//
//    private void setUpTabTitles(){
//
//        for(int i = 0; i < 10; i++){
//            this.collectionTitles.add("ToDo" + String.valueOf(i+1));
//        }
//
//    }
//
//    public List<List<ToDo>> getCollection() {
//        return collection;
//    }
//
//    public void setNewSubCollectionAtPosition(ToDo newItem, int position){
//        Log.d(TAG, collection.get(position).toString());
//        collection.get(position).add(newItem);
//        Log.d(TAG, collection.get(position).toString());
//    }
//
//    // delete all isDone == true items in a list
//    public void deleteAllDoneItemsAtPosition(int position){
//        Log.d(TAG, collection.get(position).toString());
//        int i = 0;
//        while(i < collection.get(position).size()){
//            if(collection.get(position).get(i).isDone()){
//                collection.get(position).remove(i);
//            }
//            else{
//                i += 1;
//            }
//        }
//        Log.d(TAG, collection.get(position).toString());
//    }
//
//    public void incrementSizeOfCollection(String newTabTitle){
//        List<ToDo> newSubCollection = new ArrayList<>();
//        this.collection.add(newSubCollection);
//        Log.d(TAG, String.valueOf(collection.size()));
//
//        collectionTitles.add(newTabTitle);
//    }
//
//    public List<String> getCollectionTitles() {
//        return collectionTitles;
//    }
//
//    public void setCollectionTitles(List<String> collectionTitles) {
//        this.collectionTitles = collectionTitles;
//    }
//}
