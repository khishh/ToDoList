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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.R;
import com.example.todo.databinding.FragmentItemBinding;
import com.example.todo.model.ToDo;
import com.example.todo.util.CustomEditText;
import com.example.todo.util.KeyBoardVisibilityListener;
import com.example.todo.util.LinearLayoutManagerWithSmoothScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * ItemFragment
 *
 * 1. A fragment to show all To-Dos belong to the Tab, currently displayed in TabLayout.
 *
 * 2. Allow user to add/edit/delete To-Do(s).
 *  - Since Add and Delete FABs is owned by HomeFragment, this class will be notified when a user clicks
 *  them and make actions.
 *
 * 3. Control the visibility of the user input field, composed of EditText, where a user can type content to
 * edit the existing To-Do or add new To-Do, and ImageButton, where a user can request ItemViewModel to
 * execute the operation of editing or adding To-Do.
 *
 */
public class ItemFragment extends Fragment
    implements RecyclerItemOnClickListener{

    private static final String TAG = "ItemFragment";
    public static final String KEY_TAB_ID = "KEY_TAB_ID";

    private FragmentItemBinding binding;
    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;

    // tabId
    private int tabId;

    // position of the item in a list clicked recently
    private int lastClickedItemPosition;

    /**
     * =====  Layout components  =====
     */
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private CustomEditText editText;
    private ImageButton updateBtn;


    /**
     * Listeners
     */

    private KeyBoardVisibilityListener keyBoardVisibilityListener;

    public void setKeyBoardVisibilityListener(KeyBoardVisibilityListener keyBoardVisibilityListener) {
        this.keyBoardVisibilityListener = keyBoardVisibilityListener;
    }

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
                Log.e(TAG, "EditText obtained focus");
                showKeyboard(editText);
            }
            else{
                Log.e(TAG, "EditText lost focus");
                hideKeyboard(editText);
            }
        }
    };


    /**
     * If a user clicks on Add FAB Button, set the text of EditText to be empty and the adding icon.
     * Also, set lastClickedItemPosition so that when a user pushes Add button, it will insert that To-Do as
     * a new instance rather than updating the existing To-Do in database
     */
    public void showAddNewItemInput(){
        vibrate();
        editText.setText("");
        updateBtn.setImageResource(R.drawable.ic_baseline_add_2_green);

        // let position of item clicked -1 to distinguish from adding and updating a item.
        lastClickedItemPosition = -1;
        showUserInput();
    }

    /**
     * if the text in EditText is not empty, this listener will fire one of two possible actions.
     * One is calling a method to add new To-Do into Database if lastClickedItemPosition is -1.
     * Second is calling a method to update the existing To-Do inside database.
     * After this handled, hide the user input field.
     */
    private View.OnClickListener updateBtnOnCLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            vibrate();
            if(!TextUtils.isEmpty(editText.getText().toString())){

                String newToDoContent = editText.getText().toString();

                // add new To-Do Item
                if(lastClickedItemPosition == -1){
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
     * when a user clicks on the down button on the keyboard, hide the user input field.
     */
    private CustomEditText.Listener customEditTextListener = new CustomEditText.Listener() {
        @Override
        public void onKeyboardDownClicked() {
            Log.e(TAG, "onKeyboardDownClicked called");
            hideUserInput();
        }
    };

    /**
     * Constructor
     */
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

        Bundle bundle = getArguments();
        if(bundle != null) {
            tabId = bundle.getInt(KEY_TAB_ID, 0);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.loadToDoList(tabId);

        setUpUIComponents();
        setUpRecyclerView();

        observeViewModel();
    }

    private void setUpUIComponents(){
        recyclerView = binding.listRecyclerView;
        updateBtn = binding.updateBtn;
        linearLayout = binding.userInputLinearLayout;
        editText = binding.userInputEditText;

        editText.setListener(customEditTextListener);
        editText.setOnFocusChangeListener(editTextFocusChangeListener);
        updateBtn.setOnClickListener(updateBtnOnCLickListener);
    }

    private void setUpRecyclerView(){
        final LinearLayoutManagerWithSmoothScroller linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        itemAdapter = new ItemAdapter(new ArrayList<ToDo>());
        itemAdapter.setRecyclerItemOnClickListener(this);
        recyclerView.setAdapter(itemAdapter);
    }

    /**
     * Observe any changes in To-Do in database and pass new To-Dos to RecyclerViewAdapter
     * Pass the list of To-Do
     */
    private void observeViewModel(){
        itemViewModel.getmDoList().observe(getViewLifecycleOwner(), new Observer<List<ToDo>>() {
            @Override
            public void onChanged(List<ToDo> toDos) {
                Log.d(TAG, "onChanged called = " + tabId);
                itemAdapter.updateToDoList(toDos);

                // if there is no To-Dos, display the empty message
                if(toDos.size() == 0){
                    binding.todoEmptyMsg.setVisibility(View.VISIBLE);
                }
                else{
                    binding.todoEmptyMsg.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Method to insert new To-Do into database
     */
    public void addNewToDoItem(String newToDoContent){
        itemViewModel.addNewToDo(newToDoContent);
//        Toast.makeText(linearLayout.getContext(), "New To-Do " + newToDoContent + " added", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to update the existing To-Do inside database
     */
    private void updateToDoItem(String newToDoContent){
        // need to compute the reversePosition since displaying in the reversed order
        // -> if positionItem(user clicked position) is 0, then the item position needed to change is the last element in the List.
        int reversePosition = itemAdapter.getItemCount() - lastClickedItemPosition - 1;
        itemViewModel.updateToDoContentAtPosition(reversePosition, newToDoContent);
    }

    /**
     * Method to delete all To-Dos which are marked as Complete
     */
    public void deleteAllDoneItems(){
        vibrate();
        itemViewModel.removeAllDoneToDo();
    }

    // how to show keyboard programmatically
    // https://stackoverflow.com/questions/39228245/how-to-show-soft-keyboard-perfectly-in-fragment-in-android
    private void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(keyBoardVisibilityListener != null){
            keyBoardVisibilityListener.keyboardVisibilityChange(false);
        }
    }

    /**
     * show the user input field and request focus on its EditText
     */
    private void showUserInput(){

        if(keyBoardVisibilityListener != null){
            keyBoardVisibilityListener.keyboardVisibilityChange(true);
        }
        linearLayout.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);

        editText.requestFocus();
    }

    /**
     * hide the user input field and clear focus on its EditText
     */
    public void hideUserInput(){
        if(linearLayout == null){
            return;
        }
        linearLayout.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
        updateBtn.setVisibility(View.GONE);

        editText.clearFocus();
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }

    /**
     * RecyclerItemOnClickListener interface
     *
     * onContent(position) will be triggered when a user click TextView holding content of a To-Do in the recyclerview.
     * onContent(position) will handle the visibility of LinearLayout containing EditText where
     * a user can add/update To-Do class.
     * If LinearLayout is not visible, display it and let its EditText have a content of To-Do class.
     * If already visible, hide it from screen.
     */

    @Override
    public void onContentClick(int position) {

        vibrate();
        // keep the position of item clicked and will be used for smoothScroll in setVisibilityListener
        lastClickedItemPosition = position;

        if(linearLayout.getVisibility() == View.GONE){

            showUserInput();

            // showing recent items on the top and old items on the bottom
            int reversePosition = itemAdapter.getItemCount() - lastClickedItemPosition - 1;
            updateBtn.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);

            if(lastClickedItemPosition != -1)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(lastClickedItemPosition);
                    }
                },350);

            // set content of To-Do before users can edit
            editText.setText(itemViewModel.getToDoContentAtPosition(reversePosition));
        }
        else
            hideUserInput();
    }

    /**
     * onIsDoneClick(position, isDone) will be called when a user click on the isDone button.
     * Order ItemViewModel to update isDone (boolean value) of the clicked To-Do with newIsDone
     */
    @Override
    public void onIsDoneClick(int position, boolean newIsDone) {
        vibrate();
        itemAdapter.setModifiedToDoPos(position);
        int reversePosition = itemAdapter.getItemCount() - position - 1;
        itemViewModel.updateToDoIsDoneAtPosition(reversePosition, newIsDone);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "ItemFragment onStart");
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