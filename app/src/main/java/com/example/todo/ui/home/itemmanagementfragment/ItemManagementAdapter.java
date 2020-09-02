package com.example.todo.ui.home.itemmanagementfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.example.todo.databinding.FragmentItemManagementItemBinding;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ItemManagementAdapter extends RecyclerView.Adapter<ItemManagementAdapter.ViewHolder> {

    private static final String TAG = ItemManagementAdapter.class.getSimpleName();

    FragmentItemManagementItemBinding binding;

    private List<ToDo> toDoList;
    private List<Boolean> isSelected = new ArrayList<>();

    private Listener listener;

    interface Listener{
        void onSortBtnClick(ViewHolder viewHolder);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ItemManagementAdapter(List<ToDo> toDos){
        this.toDoList = toDos;
    }

    public void updateToDos(List<ToDo> toDos){
        toDoList.clear();
        toDoList.addAll(toDos);
        notifyDataSetChanged();

        isSelected.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_management_item, parent, false);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = FragmentItemManagementItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        int reversePosition = getItemCount() - position - 1;
        final ToDo toDo = toDoList.get(reversePosition);

        holder.content.setText(toDo.getContent());

        if(toDo.isSelected()){
            holder.checkBtn.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
            holder.checkBtn.setBackgroundColor(holder.checkBtn.getResources().getColor(R.color.colorPrimary));
        }
        else{
            holder.checkBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
            holder.checkBtn.setBackgroundColor(holder.checkBtn.getResources().getColor(R.color.white_bg));
        }

        holder.sortBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

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

        holder.checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toDo.isSelected()){
                    holder.checkBtn.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                    holder.checkBtn.setBackgroundColor(v.getResources().getColor(R.color.white_bg));
                    toDo.setSelected(false);

                }
                else{
                    holder.checkBtn.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                    holder.checkBtn.setBackgroundColor(v.getResources().getColor(R.color.colorPrimary));
                    toDo.setSelected(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        ImageButton checkBtn;
        TextView content;
        ImageButton sortBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            checkBtn = itemView.findViewById(R.id.item_management_check_button);
            content = itemView.findViewById(R.id.item_management_content);
            sortBtn = itemView.findViewById(R.id.item_management_sort);
        }
    }
}
