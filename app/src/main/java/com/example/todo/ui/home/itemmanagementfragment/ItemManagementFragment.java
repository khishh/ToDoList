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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.databinding.FragmentItemManagementBinding;
import com.example.todo.model.Tab;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * ItemManagementFragment
 *
 * 1. Display all To-Dos belong to the Tab where users were browsing right before.
 *
 * 2. This is the Fragment where a user can select multiple items of To-Dos and delete or move them to other Tab.
 *    A user can also sort the order of To-Dos by dragging and dropping items.
 *
 * 3. Allow users to drag items inside RecyclerView with the help of customized ItemTouchHelper.SimpleCallBack.
 *    Drag behavior is used for sorting the order of To-Dos.
 *
 */

public class ItemManagementFragment extends Fragment
    implements RecyclerItemOnClickListener{

    private static final String TAG = "ItemManagementFragment";
    private static final String KEY_TAB_ID = "tabId";

    private int tabId;

    private ItemManagementViewModel itemManagementViewModel;
    private MoveToDoDialog dialog;

    private FragmentItemManagementBinding binding;
    private ItemManagementAdapter adapter;
    private ItemTouchHelper helper;

    /**
     * Listener
     */

    private MoveToDoDialogClickListener dialogClickListener = new MoveToDoDialogClickListener() {
        @Override
        public void onDialogClick(int targetTabId) {
            Log.d(TAG, "onDialogClick clicked");
            itemManagementViewModel.moveToDoToOtherTab(targetTabId, adapter.getToDoList());
        }
    };

    /**
     * implements methods of itemmanagementfragment.RecyclerItemOnClickListener interface
     */

    // this method will be fired whenever a user clicks on Sort button on each item in the RecyclerView.
    // When a user clicks on Sort button, dragging action starts immediately.
    @Override
    public void onSortBtnClick(ItemManagementAdapter.ViewHolder viewHolder) {
        startDragging(viewHolder);
    }

    // this method will be fired whenever a user clicks on Check button on each item in the RecyclerView.
    // Vibrate the device.
    @Override
    public void onCheckBtnClicked() {
        vibrate();
    }

    /**
     * Inner class
     *
     * ItemTouchHelper.SimpleCallBack extended class to allow users to swipe/drag & drop items in RecyclerView
     * Customize the behaviors of swipe and drag action.
     *
     */

    //  https://stackoverflow.com/questions/35920584/android-how-to-catch-drop-action-of-itemtouchhelper-which-is-used-with-recycle
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
            adapter.swapToDo(fromPos, toPos);
            adapter.notifyItemMoved(fromPos, toPos);

            vibrate();
            Log.d(TAG, "MOVE finished");
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

        // when a user select an item and start dragging it, set its alpha to 0.5f.
        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if(viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG){
                viewHolder.itemView.setAlpha(0.5f);
            }
        }

        // when a user finished dragging it, set its alpha back, and notify ViewModel to update database to keep the order.
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            itemManagementViewModel.updateToDoOrder(adapter.getToDoList());
            Log.e(TAG, "Dragging stopped");
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        public boolean isLongPressDragEnabled() { return false; }
    }

    /**
     * Constructor
     */

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            tabId = getArguments().getInt(KEY_TAB_ID);
        }

        // implements an behavior of Back Button on the device
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity)requireActivity()).popOffFragment();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_management, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        itemManagementViewModel = new ViewModelProvider(this).get(ItemManagementViewModel.class);
        itemManagementViewModel.loadTabs();
        itemManagementViewModel.loadToDoList(tabId);

        setUpUIComponent();
        setUpRecyclerView();
        observeViewModel();
    }

    private void setUpUIComponent(){
        TextView closeTextView = binding.itemManagementClose;
        Button moveBtn = binding.itemManagementMoveTodoBtn;
        Button deleteBtn = binding.itemManagementDeleteTodoBtn;

        // when a user clicks on "Close" text, go back to the previous fragment (HomeFragment)
        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                ((MainActivity)requireActivity()).popOffFragment();
            }
        });

        // when a user clicks on Move button and at least selects one To-Do,
        // show MoveToDoDialog where a user can choose a Tab where all selected To-Dos will move to.
        moveBtn.setOnClickListener(new View.OnClickListener() {
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

        // when a user clicks on Move button and at least selects one To-Do,
        // tell ViewModel to delete all selected To-Dos inside database.
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrate();

                boolean isAtLeastOneSelected = itemManagementViewModel.isToDoSelected(adapter.getToDoList());

                if(isAtLeastOneSelected) {
                    itemManagementViewModel.deleteSelectedToDo(adapter.getToDoList());
                }
                else {
                    Toast.makeText(getContext(), "Please select at least one ToDo make an action", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpRecyclerView(){
        RecyclerView recyclerView = binding.itemManagementRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ItemManagementAdapter(new ArrayList<ToDo>());
        adapter.setRecyclerItemOnClickListener(this);
        recyclerView.setAdapter(adapter);

        // only allows drags behavior in this app
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
                adapter.updateToDos(toDos);
            }
        });

        itemManagementViewModel.getTabs().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                Log.e(TAG, "List<Tab> updated");
            }
        });
    }

    // Tell helper that dragging event starts.
    private void startDragging(ItemManagementAdapter.ViewHolder viewHolder){
        vibrate();
        helper.startDrag(viewHolder);
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }
}

