package com.example.todo.ui.home.tabmanagementfragment;

import com.example.todo.model.Tab;

/**
 * RecyclerItemOnClickListener interface
 * This interface methods will be fired when an UI component of each row inside RecyclerView is clicked
 */
public interface RecyclerItemOnClickListener {
    void onSortBtnClick(TabManagementAdapter.ViewHolder viewHolder);
    void onSwipeDeleteBack(int position);
    void onDeleteBtnClick(int position, Tab deleteTab);
    void onEditBtnClick(int position, String content);
}
