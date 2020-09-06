package com.example.todo.ui.home.tabmanagementfragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabManagementAdapter extends RecyclerView.Adapter<TabManagementAdapter.ViewHolder> {

    private static final String TAG = "TabManagementAdapter";

    private ArrayList<Tab> tabList;

    private int deleteMovedPos = -1;

    private Listener listener;

    interface Listener{
        void onSortBtnClick(ViewHolder viewHolder);
        void onSwipeDeleteBack(int position);
        void deleteTabAtPosition(int position, Tab deleteTab);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public TabManagementAdapter(ArrayList<Tab> tabList){
        this.tabList = tabList;
    }

    public void updateTabList(List<Tab> tabList, boolean needToUpdateAdapterData){

        if(needToUpdateAdapterData) {
            this.tabList.clear();
            this.tabList.addAll(tabList);
        }

        notifyDataSetChanged();
    }

    public ArrayList<Tab> getTabList() {
        return tabList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_tab_management_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Tab tab = tabList.get(position);

        holder.content.setText(tab.getTabTitle());

        holder.sortButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if(deleteMovedPos != -1){
                    if(listener != null){
                        listener.onSwipeDeleteBack(deleteMovedPos);
                        deleteMovedPos = -1;
                    }
                }

                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "DOWN");
                        if(listener != null){
                            listener.onSortBtnClick(holder);
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "MOVE");

                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG, "CANCEL");
                        return true;

                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "UP");
                        return true;

                    default:
                        return false;
                }
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "deleteBtn Clicked at " + deleteMovedPos);

                if(deleteMovedPos != -1){
                    if(listener != null){
                        listener.onSwipeDeleteBack(deleteMovedPos);
                        deleteMovedPos = -1;
                    }
                }

                float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, v.getResources().getDisplayMetrics());
                holder.linearLayout.animate().translationX(-x).setDuration(300).start();

                deleteMovedPos = position;

                Log.e(TAG, "deleteBtn Clicked at " + deleteMovedPos);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteMovedPos != -1){
                    if(listener != null){
                        listener.onSwipeDeleteBack(deleteMovedPos);
                        deleteMovedPos = -1;
                    }
                }
            }
        });

        holder.deleteMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "deleteMsg Clicked");
                if(listener != null){
                    listener.onSwipeDeleteBack(position);
                    listener.deleteTabAtPosition(position, tabList.get(position));
                    deleteMovedPos = -1;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tabList.size();
    }

    public void swapTabList(int fromPos, int toPos){
        Log.d(TAG, "before swapped : " + tabList.get(fromPos).getTabTitle() + " " + tabList.get(toPos).getTabTitle());
        Tab a = tabList.get(fromPos);
        tabList.set(fromPos, tabList.get(toPos));
        tabList.set(toPos, a);
        Log.d(TAG, "after swapped : " + tabList.get(fromPos).getTabTitle() + " " + tabList.get(toPos).getTabTitle());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;
        public ImageButton deleteBtn;
        public ImageButton editBtn;
        public ImageButton sortButton;
        public Button deleteMsg;
        public TextView content;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            linearLayout = itemView.findViewById(R.id.tab_management_linear_layout);
            deleteBtn = itemView.findViewById(R.id.tab_management_delete);
            editBtn = itemView.findViewById(R.id.tab_management_edit);
            sortButton = itemView.findViewById(R.id.tab_management_sort);
            deleteMsg = itemView.findViewById(R.id.tab_management_delete_msg);
            content = itemView.findViewById(R.id.tab_management_content);
        }
    }
}
