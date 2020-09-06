package com.example.todo.ui.home.tabmanagementfragment;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabManagementFragment extends Fragment {

    private static final String TAG = "TabManagementFragment";

    private boolean needToUpdateAdapterData = true;

    private TabManagementViewModel viewModel;

    private TabManagementAdapter adapter = new TabManagementAdapter(new ArrayList<Tab>());

    private TabManagementItemCallBack callBack;

    private ItemTouchHelper helper;

    private LinearLayoutManager linearLayoutManager;

    private ImageView tabCloseBtn;

    private ImageButton addTabBtn;

    private EditText userInputTab;

    // ui components
    private RecyclerView recyclerView;

    public static TabManagementFragment getInstance(){
        TabManagementFragment fragment = new TabManagementFragment();
        return fragment;
    }

    public class TabManagementItemCallBack extends ItemTouchHelper.SimpleCallback {

        public TabManagementItemCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            final int fromPos = viewHolder.getAbsoluteAdapterPosition();
//            final int toPos = target.getAbsoluteAdapterPosition();

//            adapter.swapTabList(fromPos, toPos);

//            adapter.notifyItemMoved(fromPos, toPos);

//            Log.d(TAG, fromPos + " " + toPos);
//
//
//            viewModel.updateTabList(fromPos, toPos);
            return true;
        }

        @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

            needToUpdateAdapterData = false;
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
            if(viewHolder != null){
                viewHolder.itemView.setAlpha(1.0f);
            }
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
    }


    public TabManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(viewModel.isDeleted()){
                    Log.e(TAG, "isDeleted");
                    ((MainActivity)requireActivity()).popOffFragmentAndResetHomeFragment();
                }
                else {
                    Log.e(TAG, "is not Deleted");
                    ((MainActivity) requireActivity()).popOffFragment();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {


        viewModel = new ViewModelProvider(this).get(TabManagementViewModel.class);
        viewModel.retrieveTabList();

        recyclerView = view.findViewById(R.id.tab_manage_recyclerView);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new TabManagementAdapter.Listener() {
            @Override
            public void onSortBtnClick(TabManagementAdapter.ViewHolder viewHolder) {
                startDragging(viewHolder);
            }

            @Override
            public void onSwipeDeleteBack(int position) {
                Log.d(TAG, "onSwipeDeleteBack pos = " + position);
                TabManagementAdapter.ViewHolder viewHolder = (TabManagementAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                viewHolder.linearLayout.animate().translationX(0).setDuration(300).start();
            }

            @Override
            public void deleteTabAtPosition(int position, Tab deleteTab) {
                final int deletePos = position;
                adapter.notifyItemRemoved(deletePos);
                needToUpdateAdapterData = true;
                viewModel.deleteTab(deleteTab);
            }
        });


        tabCloseBtn = view.findViewById(R.id.tab_manage_close);
        tabCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.isDeleted()){
                    Log.e(TAG, "isDeleted");
                    ((MainActivity)requireActivity()).popOffFragmentAndResetHomeFragment();
                }
                else {
                    Log.e(TAG, "is not Deleted");
                    ((MainActivity) requireActivity()).popOffFragment();
                }
            }
        });

        userInputTab = view.findViewById(R.id.tab_management_edit_text);
        addTabBtn = view.findViewById(R.id.tab_management_add_tab);
        addTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTabTitle = userInputTab.getText().toString();

                if(!TextUtils.isEmpty(newTabTitle)){
                    needToUpdateAdapterData = true;
                    viewModel.addNewTab(newTabTitle);
                    userInputTab.setText("");
                }
                else{
                    Toast.makeText(getContext(), "Tab's title is empty", Toast.LENGTH_SHORT).show();
                }

                userInputTab.clearFocus();
                hideKeyboard(userInputTab);
            }
        });

        callBack = new TabManagementItemCallBack(
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                0);
        helper = new ItemTouchHelper(callBack);
        helper.attachToRecyclerView(recyclerView);

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getmTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                adapter.updateTabList(tabs, needToUpdateAdapterData);
            }
        });
    }

    private void startDragging(TabManagementAdapter.ViewHolder viewHolder){
        helper.startDrag(viewHolder);
    }


    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }
}