package com.example.todo.ui.home.itemfragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.databinding.FragmentItemBinding;
import com.example.todo.model.ToDo;
import com.example.todo.util.LinearLayoutManagerWithSmoothScroller;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends Fragment {

    private static final String TAG = "ItemFragment";
    public static final String ARG_OBJECT = "object";

    private FragmentItemBinding binding;
    private ItemViewModel itemViewModel;
    private ItemAdapter adapter;

    /**
     * =====  Listeners  =====
     */

    /**
     * onClick(position) will be triggered when a user click an item in the recyclerview.
     * onClick(position) will handle the visibility of LinearLayout containing EditText where
     * a user can add/update To-Do class.
     * If LinearLayout is not visible, display it and let its EditText have a content of To-Do class.
     * If already visible, hide it from screen.
     */
    private ItemAdapter.Listener userClickListener = new ItemAdapter.Listener() {
        @Override
        public void onClick(int position) {
            // Log.d(TAG, "position = " + position);

            // keep the position of item clicked and will be used for smoothScroll in setVisibilityListener
            positionItem = position;

            if(linearLayout.getVisibility() == View.GONE){

                showUserInput();

                // showing recent items on the top and old items on the bottom
                int reversePosition = adapter.getItemCount() - positionItem - 1;
                updateBtn.setText("Update");

                if(positionItem != -1)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(positionItem);
                        }
                    },300);

                editText.setText(itemViewModel.getToDoContentAtPosition(reversePosition));
            }
            else
                hideUserInput();
        }
    };

    /**
     * onFocusChange(v, hasFocus) will be called when EditText inside LinearLayout gains or loses a focus.
     * This will handle show/hide softInput according to the state of a focus.
     * If EditText lost a focus, call a method to hide soft input.
     * If EditText gained a focus, call a method to open soft input.
     */
    private View.OnFocusChangeListener editTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                    Log.d(TAG, "EditText obtained focus");
                Toast.makeText(getContext(), "EditText obtained focus", Toast.LENGTH_SHORT).show();
                showKeyboard(editText);
            }
            else{
                    Log.d(TAG, "EditText lost focus");
                Toast.makeText(getContext(), "EditText lost focus", Toast.LENGTH_SHORT).show();
                hideKeyboard(editText);
            }
        }
    };

    /**
     * When add Button is clicked, set up texts of EditText and Button before show them on the screen
     * followed by showUserInput()
     */
    private View.OnClickListener addBtnOnCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Log.d(TAG, String.valueOf(positionItem));
            editText.setText("");
            updateBtn.setText("Add");

            // let position of item clicked -1 to distinguish from adding and updating a item.
            positionItem = -1;
            showUserInput();
        }
    };

    /**
     * Delete Button click will delete all To-Do items in Recyclerview with a check-mark
     */
    private View.OnClickListener deleteBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteAllDoneItems();
        }
    };

    private View.OnClickListener updateBtnOnCLickListener = new View.OnClickListener() {
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
                hideUserInput();
            }
        }
    };

    /**
     * =====  Layout components  =====
     */
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private EditText editText;
    private Button updateBtn;
    private FloatingActionButton addBtn;
    private FloatingActionButton deleteBtn;

    // tabId
    private int tabId;

    // position of the item in a list clicked recently
    private int positionItem;

    public ItemFragment() {
        // Required empty public constructor
        Log.d(TAG, "ItemFragment created");
    }

    public ItemFragment(int tabId){
        this.tabId = tabId;
        Log.d(TAG, "ItemFragment created TabId = " + tabId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item, container, false);

        recyclerView = binding.listRecyclerView;
        updateBtn = binding.updateBtn;
        editText = binding.userInputEditText;
        addBtn = binding.btnAddTodo;
        deleteBtn = binding.btnDeleteTodo;
        linearLayout = binding.userInputLinearLayout;

        Bundle bundle = getArguments();
        if(bundle != null)
            tabId = bundle.getInt(ARG_OBJECT, 0);
//        Log.d(TAG, "tabId " + tabId);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onViewCreated tabId = " + tabId);

        setUpRecyclerView();

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.loadToDoList(tabId);

        observeViewModel();
        attachOnClickListenerToViews();
    }



    private void setUpRecyclerView(){
        final LinearLayoutManagerWithSmoothScroller linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ItemAdapter(new ArrayList<ToDo>());
        adapter.setListener(userClickListener);
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel(){
        itemViewModel.getMDoList().observe(getViewLifecycleOwner(), new Observer<List<ToDo>>() {
            @Override
            public void onChanged(List<ToDo> toDos) {
                Log.d(TAG, "onChanged called = " + tabId);
                Log.d(TAG, toDos.toString());
                adapter.updateToDoList(toDos);
            }
        });
    }

    private void attachOnClickListenerToViews(){
        editText.setOnFocusChangeListener(editTextFocusChangeListener);
        addBtn.setOnClickListener(addBtnOnCLickListener);
        deleteBtn.setOnClickListener(deleteBtnOnClickListener);
        updateBtn.setOnClickListener(updateBtnOnCLickListener);
    }

    private void addNewToDoItem(String newToDoContent){
        itemViewModel.addNewToDo(newToDoContent);
        Toast.makeText(linearLayout.getContext(), "New ToDo " + newToDoContent + " added", Toast.LENGTH_SHORT).show();
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

    private void showKeyboard(View view){
        // how to show keyboard programmatically
        // https://stackoverflow.com/questions/39228245/how-to-show-soft-keyboard-perfectly-in-fragment-in-android

        InputMethodManager inputMethodManager = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void showUserInput(){
        linearLayout.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);

        editText.requestFocus();

        addBtn.setVisibility(View.GONE);
        deleteBtn.setVisibility(View.GONE);
    }

    private void hideUserInput(){
        linearLayout.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        updateBtn.setVisibility(View.GONE);

        editText.clearFocus();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }, 50);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ItemFragment onStart");

        /*
        Log.d(TAG, "tabId == " + tabId + " mTabId == " + itemViewModel.getmTabId().getValue());
        if(itemViewModel != null && tabId != itemViewModel.getmTabId().getValue()){
            Log.d(TAG, "ItemFragment onStart entered");
            itemViewModel.loadToDoList(tabId);
        }
         */
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ItemFragment onResume");
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
            hideUserInput();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "ItemFragment onDestroyView");
    }
}
