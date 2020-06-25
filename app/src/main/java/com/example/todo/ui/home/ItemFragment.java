package com.example.todo.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.model.ToDo;
import com.example.todo.model.ToDoCollection;
import com.example.todo.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment {

    private static final String TAG = "ItemFragment";

    public static final String ARG_OBJECT = "object";

    private FrameLayout frameLayout;

    // for test
    private ArrayList<ToDo> doList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private LinearLayout linearLayout;
    private EditText editText;
    private Button updateBtn;

    private int position;

    private ItemViewModel itemViewModel;

    public ItemFragment() {
        // Required empty public constructor
        Log.d(TAG, "ItemFragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_item, container, false);
        Bundle bundle = getArguments();
        position = bundle.getInt(ARG_OBJECT);

//        testSetUp();
        recyclerView = view.findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(this, new ArrayList<ToDo>());
//        adapter = new ItemAdapter(ToDoCollection.getInstance().getCollection().get(position));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // for test
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.setToDoList(position);
        observeViewModel();

        // enable non EditText and Button inside this layout to hide keyboard
        setUpUI(view.getRootView());

        frameLayout = view.findViewById(R.id.item_fragment_container);
        updateBtn = view.findViewById(R.id.update_btn);
        editText = view.findViewById(R.id.user_input_edit_text);

        linearLayout = view.findViewById(R.id.user_input_linear_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Linearlayout touched", Toast.LENGTH_SHORT).show();
            }
        });

        setVisibilityListener();
    }


    private void observeViewModel(){

        itemViewModel.getDoList().observe(getViewLifecycleOwner(), new Observer<List<ToDo>>() {
            @Override
            public void onChanged(List<ToDo> toDos) {
                adapter.updateToDoList(toDos);
            }
        });
    }

    private void setUpUI(View view){

        // if view touched is the container of EditText and Buttons, skip setting onTouchListener
        if(view == view.getRootView().findViewById(R.id.user_input_linear_layout)){
            Log.d(TAG, "Found seeking linear layout");
        }
        else if(!(view instanceof EditText) && !(view instanceof Button)){
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }
            });
        }

        if(view instanceof ViewGroup){
            for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
                View innerView = ((ViewGroup)view).getChildAt(i);
                setUpUI(innerView);
            }
        }
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    private void hideEditDialog(){
        Log.d(TAG, String.valueOf("Before " +linearLayout.getVisibility()));
        if(linearLayout.getVisibility() == View.VISIBLE){
            Log.d(TAG, "pass here");
            linearLayout.setVisibility(View.GONE);
            updateBtn.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
        }
        Log.d(TAG, "after " + String.valueOf(linearLayout.getVisibility()));
    }

    private void setVisibilityListener(){
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, String.valueOf(frameLayout.getRootView().getHeight()) + " " + frameLayout.getHeight());
                int heightDiff = frameLayout.getRootView().getHeight() - frameLayout.getHeight();
                if (heightDiff < Util.dpToPx(frameLayout.getContext(), 200)) {
                    linearLayout.setVisibility(View.GONE);
                }
                else{
                    linearLayout.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
