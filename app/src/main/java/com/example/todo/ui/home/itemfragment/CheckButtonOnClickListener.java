package com.example.todo.ui.home.itemfragment;

import android.view.View;

import com.example.todo.model.ToDo;

public interface CheckButtonOnClickListener {
    void onCheckButtonClick(View view, ToDo toDo);
}
