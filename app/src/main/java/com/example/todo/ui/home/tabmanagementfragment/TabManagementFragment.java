package com.example.todo.ui.home.tabmanagementfragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.databinding.FragmentTabManagementBinding;
import com.example.todo.model.Tab;
import com.example.todo.util.CustomEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * TabManagementFragment
 *
 * 1. Display all Tabs.
 *
 * 2. Allow users to add/edit/sort/delete Tab(s).
 */

public class TabManagementFragment extends Fragment
    implements RecyclerItemOnClickListener{

    private static final String TAG = TabManagementFragment.class.getSimpleName();

    private TabManagementViewModel viewModel;
    private FragmentTabManagementBinding binding;
    private TabManagementAdapter adapter;

    private ItemTouchHelper helper;

    private int editPosition;

    /**
     * UI components
     */
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private CustomEditText tabEditEditText;
    private CustomEditText tabAddEditText;
    private ImageButton editButton;

    public static TabManagementFragment getInstance(){
        return new TabManagementFragment();
    }

    public class TabManagementItemCallBack extends ItemTouchHelper.SimpleCallback {

        public TabManagementItemCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

            adapter.swapTabList(fromPos, toPos);
            adapter.notifyItemMoved(fromPos, toPos);
            vibrate();
            Log.d(TAG, "MOVE finished");
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                viewHolder.itemView.setAlpha(0.5f);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewModel.saveTabOrder(adapter.getTabList());
            Log.e(TAG, "Dragging stopped");
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
    }

    /**
     * Listeners
     */


    /**
     * Observes an focus state of EditText.
     * When it loses focus, close the keyboard and show the keyboard when it obtains focus.
     */
    private View.OnFocusChangeListener editTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                Log.e(TAG, "EditText obtained focus");
                showKeyboard(tabEditEditText);
            }
            else{
                Log.e(TAG, "EditText lost focus");
                hideKeyboard(tabEditEditText);
            }
        }
    };

    /**
     * A method fired when a user clicks on Done button on the keyboard
     * Set for tabEditEditText. When a user clicks on Done button, hide linearLayout by hideUserInput.
     */
    private CustomEditText.Listener tabEditCustomEditTextListener = new CustomEditText.Listener() {
        @Override
        public void onKeyboardDownClicked() {
            Log.e(TAG, "onKeyboardDownClicked called");
            hideUserInput();
        }
    };

    /**
     * A method fired when a user clicks on Done button on the keyboard
     * Set fot tabAddEditText. When a user clicks on Done button, remove a focus from tabAddEditText.
     */
    private CustomEditText.Listener tabAddCustomEditTextListener = new CustomEditText.Listener() {
        @Override
        public void onKeyboardDownClicked() {
            Log.e(TAG, "onKeyboardDownClicked called");
            tabAddEditText.clearFocus();
        }
    };

    public TabManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // implements the behavior of onBackPress
        // if users added/sorted/edited Tab(s), call popOffFragmentAndResetHomeFragment() to refresh HomeFragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(viewModel.isDeleterOrSortedOrEditedOrAdded()){
                    Log.e(TAG, "isDeleted or sorted");
                    ((MainActivity)requireActivity()).popOffFragmentAndResetHomeFragment();
                }
                else {
                    Log.e(TAG, "is not Deleted");
                    ((MainActivity)requireActivity()).popOffFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_tab_management,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TabManagementViewModel.class);
        viewModel.retrieveTabList();

        setUpUIComponents();
        setUpRecyclerView();
        observeViewModel();
    }

    private void setUpUIComponents(){
        // reference to All Ui Components
        recyclerView = binding.tabManageRecyclerView;
        linearLayout = binding.tabManageUserEdit;
        tabEditEditText = binding.tabManagementEditEditText;
        editButton = binding.tabManageUserImageButton;
        tabAddEditText = binding.tabManagementAddEditText;
        ImageButton addTabBtn = binding.tabManagementAddTab;
        ImageView tabCloseBtn = binding.tabManageClose;

        tabEditEditText.setOnFocusChangeListener(editTextFocusChangeListener);
        tabEditEditText.setListener(tabEditCustomEditTextListener);
        tabAddEditText.setOnFocusChangeListener(editTextFocusChangeListener);
        tabAddEditText.setListener(tabAddCustomEditTextListener);

        tabCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.isDeleterOrSortedOrEditedOrAdded()){
                    Log.e(TAG, "isDeleted");
                    ((MainActivity)requireActivity()).popOffFragmentAndResetHomeFragment();
                }
                else {
                    Log.e(TAG, "is not Deleted");
                    ((MainActivity) requireActivity()).popOffFragment();
                }
            }
        });

        addTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTabTitle = tabAddEditText.getText().toString();

                if(!TextUtils.isEmpty(newTabTitle)){
                    viewModel.addNewTab(newTabTitle);
                    tabAddEditText.setText("");
                    tabAddEditText.clearFocus();
                }
                else{
                    Toast.makeText(getContext(), "Tab's title is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTabTitle = tabEditEditText.getText().toString();
                viewModel.editTabName(editPosition, newTabTitle);
                hideUserInput();
            }
        });
    }

    private void setUpRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TabManagementAdapter(new ArrayList<Tab>());
        recyclerView.setAdapter(adapter);

        adapter.setRecyclerItemOnClickListener(this);

        TabManagementItemCallBack callBack = new TabManagementItemCallBack(
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                0);
        helper = new ItemTouchHelper(callBack);
        helper.attachToRecyclerView(recyclerView);
    }

    private void observeViewModel(){
        viewModel.getmTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                adapter.updateTabList(tabs);
            }
        });
    }

    private void startDragging(TabManagementAdapter.ViewHolder viewHolder){
        helper.startDrag(viewHolder);
    }

    private void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }

    private void showUserInput(){
        linearLayout.setVisibility(View.VISIBLE);
        tabEditEditText.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);

        tabEditEditText.requestFocus();
    }

    public void hideUserInput(){
        linearLayout.setVisibility(View.GONE);
        tabEditEditText.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        tabEditEditText.clearFocus();
    }

    /**
     * Implements methods from tabmanagementfragment.RecyclerItemOnClickListener
     */

    @Override
    public void onSortBtnClick(TabManagementAdapter.ViewHolder viewHolder) {
        startDragging(viewHolder);
    }

    @Override
    public void onSwipeDeleteBack(int position) {
        Log.d(TAG, "onSwipeDeleteBack pos = " + position);
        TabManagementAdapter.ViewHolder viewHolder = (TabManagementAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        assert viewHolder != null;
        viewHolder.linearLayout.animate().translationX(0).setDuration(300).start();
    }

    @Override
    public void onDeleteBtnClick(int position, Tab deleteTab) {
        adapter.notifyItemRemoved(position);
        viewModel.deleteTab(deleteTab);
    }

    @Override
    public void onEditBtnClick(int position, String content) {
        editPosition = position;
        tabEditEditText.setText(content);
        showUserInput();
    }
}