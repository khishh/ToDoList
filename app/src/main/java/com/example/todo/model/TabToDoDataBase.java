package com.example.todo.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Tab.class, ToDo.class}, version = 1)
public abstract class TabToDoDataBase extends RoomDatabase {

    private static TabToDoDataBase instance;

    public static TabToDoDataBase getInstance(Context context){

        if(instance == null){

            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TabToDoDataBase.class,
                    "tabtododatabase"
            ).build();

        }

        return instance;
    }

    public abstract TabToDoDao tabToDoDao();

}
