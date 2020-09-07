package com.example.todo.ui.home.itemfragment;

public interface RecyclerItemOnClickListener {
    void onContentClick(int position);
    void onIsDoneClick(int position, boolean isDone);
}
