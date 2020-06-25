package com.example.todo.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.todo.R;
import com.example.todo.model.ToDo;
import com.example.todo.util.Util;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    private static final String TAG = "ItemAdapter";

    private Fragment fragment;

    private List<ToDo> doList;

    private List<ToDo> toDoCollection = new ArrayList<>();

    private int[] test = {5, 4, 6, 8, 10, 15, 8, 3, 12, 2};


    // may need change since I needed fragment to add EditText below RecyclerView
    public ItemAdapter(Fragment fragment, List<ToDo> doList){
        this.fragment = fragment;
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

        Log.d(TAG, "position: " + position);
        tv.setText(doList.get(position).getContent());

        LinearLayout linearLayout = holder.view.findViewById(R.id.item_linear_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Touched at " + position, Toast.LENGTH_SHORT).show();

                // how to show keyboard programmatically
                // https://stackoverflow.com/questions/39228245/how-to-show-soft-keyboard-perfectly-in-fragment-in-android
                InputMethodManager inputMethodManager = (InputMethodManager)(v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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
