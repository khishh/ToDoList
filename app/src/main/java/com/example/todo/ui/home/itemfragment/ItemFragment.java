package com.example.todo.ui.home.itemfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.model.ToDo;
import com.example.todo.util.LinearLayoutManagerWithSmoothScroller;
import com.example.todo.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment {

    private static final String TAG = "ItemFragment";

    public static final String ARG_OBJECT = "object";

    private FrameLayout frameLayout;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private LinearLayout linearLayout;
    private EditText editText;
    private Button updateBtn;
    private FloatingActionButton addBtn;
    private FloatingActionButton deleteBtn;

    // tabId
    private int tabIndex;

    // position of the item in a list clicked recently
    private int positionItem;

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

        if(bundle != null)
            tabIndex = bundle.getInt(ARG_OBJECT, 0);

        Log.d(TAG, "tabIndex " + tabIndex );

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.list_recycler_view);

        final LinearLayoutManagerWithSmoothScroller linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ItemAdapter(new ArrayList<ToDo>());

        adapter.setListener(new ItemAdapter.Listener() {
            @Override
            public void onClick(final int position) {
                Log.d(TAG, "position = " + position);

                // keep the position of item clicked and will be used for smoothScroll in setVisibilityListener
                positionItem = position;

                if(linearLayout.getVisibility() == View.GONE){
                    editText.requestFocus();
                    int reversePosition = adapter.getItemCount() - positionItem - 1;
                    updateBtn.setText("Update");

                    if(positionItem != -1)
                        recyclerView.smoothScrollToPosition(positionItem);

                    editText.setText(itemViewModel.getToDoContentAtPosition(reversePosition));

                    if(positionItem != -1)
                        recyclerView.smoothScrollToPosition(positionItem);

                    showKeyboard(editText);
                }
                else{

                    editText.clearFocus();

                    hideKeyboard(editText);
                }
            }
        });
        recyclerView.setAdapter(adapter);


        // for test
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.loadToDoList(tabIndex);

        observeViewModel();

        frameLayout = view.findViewById(R.id.item_fragment_container);
        setVisibilityListener();

        updateBtn = view.findViewById(R.id.update_btn);
        editText = view.findViewById(R.id.user_input_edit_text);
        addBtn = view.findViewById(R.id.btn_add_todo);
        deleteBtn = view.findViewById(R.id.btn_delete_todo);

        linearLayout = view.findViewById(R.id.user_input_linear_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "LinearLayout touched", Toast.LENGTH_SHORT).show();
            }
        });

        attachOnClickListenerToViews();
    }

    private void attachOnClickListenerToViews(){

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, String.valueOf(positionItem));
                editText.setText("");
                updateBtn.setText("Add");

                // let position of item clicked -1 to distinguish from adding and updating a item.
                positionItem = -1;
                editText.requestFocus();
                showKeyboard(linearLayout);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllDoneItems();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(editText.getText().toString())){

                    String newToDoContent = editText.getText().toString();

                    // add new To-Do Item
                    if(positionItem == -1){
                        addNewToDoItem(newToDoContent);
                    }
                    // update existing To-Do Item
                    else{
                        updateToDoItem(newToDoContent);
                    }

                    hideKeyboard(editText);
                    editText.clearFocus();
                }
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                }
            }
        });
    }

    private void addNewToDoItem(String newToDoContent){
        itemViewModel.addNewToDo(newToDoContent);
        Toast.makeText(linearLayout.getContext(), "new ToDo " + newToDoContent + " added", Toast.LENGTH_SHORT).show();
    }

    private void updateToDoItem(String newToDoContent){
        // need to compute the reversePosition since displaying in the reversed order
        // -> if positionItem(user clicked position) is 0, then the item position needed to change is the last element in the List.
        int reversePosition = adapter.getItemCount() - positionItem - 1;
        itemViewModel.updateToDoContentAtPosition(reversePosition, newToDoContent);
    }

    private void deleteAllDoneItems(){
        itemViewModel.removeAllDoneToDo();
    }


    private void observeViewModel(){
        itemViewModel.getMDoList().observe(getViewLifecycleOwner(), new Observer<List<ToDo>>() {
            @Override
            public void onChanged(List<ToDo> toDos) {
                adapter.updateToDoList(toDos);
            }
        });
    }

    private void showKeyboard(View view){
        // how to show keyboard programmatically
        // https://stackoverflow.com/questions/39228245/how-to-show-soft-keyboard-perfectly-in-fragment-in-android
        Log.d(TAG, "Here Show keyboard");
        InputMethodManager inputMethodManager = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    private void hideKeyboard(View view){
        Log.d(TAG, "Hide keyboard");
        View currentFocused = ((Activity)view.getContext()).getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;


//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        editText.clearFocus();
    }

    // control the visibility of the user input area depending on the current size of the screen
    // --> if the height diff is more than 200(assuming that soft input is open already), so open the input area
    // --> else key board is hidden so set the input area gone
    private void setVisibilityListener(){
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "----- position " + tabIndex);
                Log.d(TAG, frameLayout.getRootView().getHeight() + " " + frameLayout.getHeight());
                int heightDiff = frameLayout.getRootView().getHeight() - frameLayout.getHeight();
                Log.d(TAG, "HeightDiff == " + heightDiff);
                if (heightDiff > frameLayout.getHeight()/2) {

                    Log.d(TAG, "onGlobalLayout -- VISIBLE passed");
                    linearLayout.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.VISIBLE);

                    if(positionItem != -1)
                        recyclerView.smoothScrollToPosition(positionItem);

                    addBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);

                }
                else{

                    Log.d(TAG, "onGlobalLayout -- GONE passed");
                    linearLayout.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    updateBtn.setVisibility(View.GONE);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addBtn.setVisibility(View.VISIBLE);
                            deleteBtn.setVisibility(View.VISIBLE);
                        }
                    }, 50);
                }
            }
        });
    }

    private void hideUserInput(){
        linearLayout.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        updateBtn.setVisibility(View.GONE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }, 50);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "ItemFragment OnStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ItemFragment destroyed");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(linearLayout.getVisibility() == View.VISIBLE){
            Log.d(TAG, "onPause hideUserInput called");
            hideUserInput();
        }

    }
}
