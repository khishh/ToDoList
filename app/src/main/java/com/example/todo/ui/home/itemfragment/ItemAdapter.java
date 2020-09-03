package com.example.todo.ui.home.itemfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.todo.R;
import com.example.todo.databinding.ItemTodoBinding;
import com.example.todo.model.ToDo;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>
        implements ListItemOnClickListener{

    private static final String TAG = "ItemAdapter";

    private Listener listener;
    private List<ToDo> doList;
    private int modifiedToDoPos;

    @Override
    public void onListItemClick(View view) {
        if(listener != null){
            LinearLayout linearLayout = (LinearLayout) view.getParent();
            TextView tv = linearLayout.findViewById(R.id.item_index);
            int position = Integer.parseInt(tv.getText().toString());
            Log.d(TAG, "onListItemClick at " + position);
            listener.onClick(position);
        }
    }


    interface  Listener{
        void onClick(int position);
        void onIsDoneClick(int position, boolean isDone);
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public void setModifiedToDoPos(int modifiedToDoPos) {
        this.modifiedToDoPos = modifiedToDoPos;
    }

    // may need change since I needed fragment to add EditText below RecyclerView
    public ItemAdapter(List<ToDo> doList){
        this.doList = doList;
    }

    public void updateToDoList(List<ToDo> newCollection){
        doList.clear();
        doList.addAll(newCollection);

        if(modifiedToDoPos != -1){
            notifyItemChanged(modifiedToDoPos);
            modifiedToDoPos = -1;
        }
        else {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTodoBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_todo, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final int reversePosition = getItemCount() - position - 1;
        final ToDo toDo = doList.get(reversePosition);
        holder.binding.setToDo(doList.get(reversePosition));
        holder.binding.setItemListener(this);
        holder.binding.setPosition(position);

        Log.d(TAG, holder.binding.getToDo().toString());

        TextView tv = holder.binding.itemTextView;
        ImageButton ib = holder.binding.itemImage;

        // able to display the newly added item in the List shows on top of the recyclerview
        // Log.d(TAG, "position: " + reversePosition);
        tv.setText(doList.get(reversePosition).getContent());

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!toDo.isDone()){
                    ((ImageButton)v).setImageResource(R.drawable.ic_check_item);
                    // change To-Do's isDone to be true
                    Log.e(TAG, toDo.toString() + " position == " + position);
                    listener.onIsDoneClick(position, true);
                }
                else{
                    ((ImageButton)v).setImageResource(R.drawable.item_circle);
                    // change To-Do's isDone to be true
                    listener.onIsDoneClick(position, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemTodoBinding binding;

        public ViewHolder(@NonNull ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @BindingAdapter("android:src")
    public static void setToDoItemDrawable(ImageButton ib, boolean isDone){
        if(isDone){
            ib.setImageResource(R.drawable.ic_check_item);
        }
        else{
            ib.setImageResource(R.drawable.item_circle);
        }
    }
}
