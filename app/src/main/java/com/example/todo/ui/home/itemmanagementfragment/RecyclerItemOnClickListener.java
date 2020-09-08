package com.example.todo.ui.home.itemmanagementfragment;

/**
 * RecyclerItemOnClickListener interface
 * This interface methods will be fired when an UI component of each row inside RecyclerView is clicked
 */
public interface RecyclerItemOnClickListener {
    void onSortBtnClick(ItemManagementAdapter.ViewHolder viewHolder);
    void onCheckBtnClicked();
}
