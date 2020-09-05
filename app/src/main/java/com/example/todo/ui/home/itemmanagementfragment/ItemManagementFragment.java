package com.example.todo.ui.home.itemmanagementfragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.databinding.FragmentItemManagementBinding;
import com.example.todo.model.Tab;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;


public class ItemManagementFragment extends Fragment{

    private static final String TAG = "ItemManagementFragment";

    private static final String KEY_TAB_ID = "tabId";

    private int tabId;
    private boolean needToUpdateAdapterData;

    private ItemManagementViewModel itemManagementViewModel;
    private MoveToDoDialog dialog;

    private RecyclerView recyclerView;

    private FragmentItemManagementBinding binding;
    ItemManagementAdapter adapter;
    ItemTouchHelper helper;

    private MoveToDoDialogClickListener dialogClickListener = new MoveToDoDialogClickListener() {
        @Override
        public void onDialogClick(int targetTabId) {
            Log.d(TAG, "onDialogClick clicked");
            needToUpdateAdapterData = true;
            itemManagementViewModel.moveToDoToOtherTab(targetTabId, adapter.getToDoList());
        }
    };

    public ItemManagementFragment() {
        // Required empty public constructor
    }

    public static ItemManagementFragment newInstance(int tabId) {
        ItemManagementFragment fragment = new ItemManagementFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TAB_ID, tabId);
        fragment.setArguments(args);
        return fragment;
    }


//    https://stackoverflow.com/questions/35920584/android-how-to-catch-drop-action-of-itemtouchhelper-which-is-used-with-recycle
    public class ItemManagementItemCallback extends ItemTouchHelper.SimpleCallback{

        public ItemManagementItemCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            final int totalNumOfToDo = adapter.getItemCount();
            final int reversedFromPos = totalNumOfToDo - fromPos - 1;
            final int reversedToPos = totalNumOfToDo - toPos - 1;

            needToUpdateAdapterData = false;
            adapter.swapToDo(reversedFromPos, reversedToPos);
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
            itemManagementViewModel.saveLastToDoOrder(adapter.getToDoList());
            Log.e(TAG, "Dragging stopped");
            if(viewHolder != null){
                viewHolder.itemView.setAlpha(1.0f);
            }
        }

        @Override
        public boolean isLongPressDragEnabled() { return false; }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            tabId = getArguments().getInt(KEY_TAB_ID);
        }
        Log.d(TAG, "onCreate: tabId == " + tabId);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity)getActivity()).popOffFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_management, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        itemManagementViewModel = new ViewModelProvider(this).get(ItemManagementViewModel.class);
        itemManagementViewModel.loadTabs();
        itemManagementViewModel.loadToDoList(tabId);

        recyclerView = binding.itemManagementRecyclerView;

        binding.itemManagementClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                ((MainActivity)getActivity()).popOffFragment();
            }
        });

        binding.itemManagementMoveTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate();

                boolean isAtLeastOneSelected = itemManagementViewModel.isToDoSelected(adapter.getToDoList());

                if (isAtLeastOneSelected) {

                    dialog = new MoveToDoDialog(itemManagementViewModel.getTabsValue());
                    dialog.setListener(dialogClickListener);

                    dialog.show(getChildFragmentManager(), null);
                }
                else{
                    Toast.makeText(getContext(), "Please select at least one ToDo make an action", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.itemManagementDeleteTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate();

                boolean isAtLeastOneSelected = itemManagementViewModel.isToDoSelected(adapter.getToDoList());

                if(isAtLeastOneSelected) {
                    needToUpdateAdapterData = true;
                    itemManagementViewModel.deleteSelectedToDo(adapter.getToDoList());
                }
                else {
                    Toast.makeText(getContext(), "Please select at least one ToDo make an action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpRecyclerView();
        observeViewModel();
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        needToUpdateAdapterData = true;
        adapter = new ItemManagementAdapter(new ArrayList<ToDo>());
        adapter.setListener(new ItemManagementAdapter.Listener() {
            @Override
            public void onSortBtnClick(ItemManagementAdapter.ViewHolder viewHolder) {
                startDragging(viewHolder);
            }

            @Override
            public void onCheckBtnClicked() {
                vibrate();
            }
        });
        recyclerView.setAdapter(adapter);

        ItemManagementItemCallback callback = new ItemManagementItemCallback(
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                0
        );

        helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    private void observeViewModel(){
        itemManagementViewModel.getmDoList().observe(getViewLifecycleOwner(), new Observer<List<ToDo>>() {
            @Override
            public void onChanged(List<ToDo> toDos) {
                Log.e(TAG, "List<ToDo> updated");
                adapter.updateToDos(toDos, needToUpdateAdapterData);
            }
        });

        itemManagementViewModel.getTabs().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                Log.e(TAG, "List<Tab> updated");
                Log.e(TAG, tabs.toString());
            }
        });
    }

    private void startDragging(ItemManagementAdapter.ViewHolder viewHolder){
        vibrate();
        helper.startDrag(viewHolder);
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }
}

