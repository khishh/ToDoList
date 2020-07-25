package com.example.todo.ui.home.itemfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.todo.R;
import com.example.todo.model.ToDo;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    private static final String TAG = "ItemAdapter";

    private Listener listener;

    private List<ToDo> doList;

    private List<ToDo> toDoCollection = new ArrayList<>();

    private int[] test = {5, 4, 6, 8, 10, 15, 8, 3, 12, 2};

    interface  Listener{
        void onClick(int position);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    // may need change since I needed fragment to add EditText below RecyclerView
    public ItemAdapter(List<ToDo> doList){
        this.doList = doList;
    }

    public void updateToDoList(List<ToDo> newCollection){
        doList.clear();
        doList.addAll(newCollection);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_todo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        TextView tv = holder.view.findViewById(R.id.item_text_view);
        ImageButton ib = holder.view.findViewById(R.id.item_image);

        // able to display the newly added item in the List shows on top of the recyclerview
        final int reversePosition = getItemCount() - position - 1;
//        Log.d(TAG, "position: " + reversePosition);
        tv.setText(doList.get(reversePosition).getContent());

        if(doList.get(reversePosition).isDone()){
            ib.setImageResource(R.drawable.ic_check_item);
            ib.setTag(R.drawable.ic_check_item);
        }
        else{
            ib.setImageResource(R.drawable.item_circle);
            ib.setTag(R.drawable.item_circle);
        }

//        LinearLayout linearLayout = holder.view.findViewById(R.id.item_linear_layout);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.d(TAG, v.getClass().toString() + " " + v.getId());

                if(listener != null){
                    listener.onClick(position);
                }
            }
        });

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer res = (Integer)((ImageButton)v).getTag();
                if(res == null || res == R.drawable.item_circle){
                    ((ImageButton)v).setImageResource(R.drawable.ic_check_item);
                    ((ImageButton)v).setTag(R.drawable.ic_check_item);

                    // change To-Do's isDone to be true
                    doList.get(reversePosition).setDone(true);
                }
                else{
                    ((ImageButton)v).setImageResource(R.drawable.item_circle);
                    ((ImageButton)v).setTag(R.drawable.item_circle);

                    // change To-Do's isDone to be true
                    doList.get(reversePosition).setDone(false);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return doList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }



}
