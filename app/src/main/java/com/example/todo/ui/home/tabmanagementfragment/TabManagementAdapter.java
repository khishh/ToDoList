package com.example.todo.ui.home.tabmanagementfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabManagementAdapter extends RecyclerView.Adapter<TabManagementAdapter.ViewHolder> {

    private ArrayList<Tab> tabList;

    public TabManagementAdapter(ArrayList<Tab> tabList){
        this.tabList = tabList;
    }

    public void updateTabList(List<Tab> tabList){
        this.tabList.clear();
        this.tabList.addAll(tabList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_tab_management_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
