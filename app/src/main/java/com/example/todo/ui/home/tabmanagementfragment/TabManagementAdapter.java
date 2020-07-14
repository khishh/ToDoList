package com.example.todo.ui.home.tabmanagementfragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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

    private ArrayList<Tab> tabList;

    private int deleteMovedPos = -1;

    private Listener listener;

    private HashMap<Integer, TabManagementAdapter.ViewHolder> holderList = new HashMap<>();

    interface Listener{
        void onClick(ViewHolder viewHolder);
        void onSwipeDeleteBack(int position);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(!holderList.containsKey(position)){
            holderList.put(position, holder);
        }

//        holder.linearLayout.bringToFront();

        holder.sortButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(deleteMovedPos != -1){
                    if(listener != null){
                        listener.onSwipeDeleteBack(deleteMovedPos);
                        deleteMovedPos = -1;
                    }
                }

                if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    if(listener != null){
                        listener.onClick(holder);
                    }
                }
                return false;
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(deleteMovedPos != -1){
                    if(listener != null){
                        listener.onSwipeDeleteBack(deleteMovedPos);
                        deleteMovedPos = -1;
                    }
                }

                float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, v.getResources().getDisplayMetrics());
                holder.linearLayout.animate().translationX(-x).setDuration(300).start();

                deleteMovedPos = position;
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

    }

    public ViewHolder getViewByPosition(int position){
        return holderList.get(position);
    }

    @Override
    public int getItemCount() {
        return tabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;
        public ImageButton deleteBtn;
        public ImageButton editBtn;
        public ImageButton sortButton;
        public Button deleteMsg;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            linearLayout = itemView.findViewById(R.id.tab_management_linear_layout);
            deleteBtn = itemView.findViewById(R.id.tab_management_delete);
            editBtn = itemView.findViewById(R.id.tab_management_edit);
            sortButton = itemView.findViewById(R.id.tab_management_sort);
            deleteMsg = itemView.findViewById(R.id.tab_management_delete_msg);
        }
    }
}
