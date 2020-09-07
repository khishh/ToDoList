package com.example.todo.ui.home.itemfragment;

/**
 * interface class to fire an action when a user clicks on UI components inside each item(row) in RecyclerView
 */
public interface RecyclerItemOnClickListener {
    void onContentClick(int position);
    void onIsDoneClick(int position, boolean isDone);
}
