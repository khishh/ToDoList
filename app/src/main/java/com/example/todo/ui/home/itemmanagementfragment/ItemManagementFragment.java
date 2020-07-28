package com.example.todo.ui.home.itemmanagementfragment;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.databinding.FragmentItemManagementBinding;
import com.example.todo.model.ToDo;

import java.util.ArrayList;
import java.util.List;


public class ItemManagementFragment extends Fragment{

    private static final String TAG = "ItemManagementFragment";

    private static final String KEY_TAB_ID = "tabId";

    private int tabId;

    private ItemManagementViewModel itemManagementViewModel;

    private RecyclerView recyclerView;

    private FragmentItemManagementBinding binding;
    ItemManagementAdapter adapter;
    ItemTouchHelper helper;

    private MoveToDoDialogClickListener dialogClickListener = new MoveToDoDialogClickListener() {
        @Override
        public void onDialogClick(int targetTabId) {
            Log.d(TAG, "onDialogClick clicked");
            itemManagementViewModel.moveToDoToOtherTab(targetTabId);
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

    public class ItemManagementItemCallback extends ItemTouchHelper.SimpleCallback{

        public ItemManagementItemCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            final int fromPos = viewHolder.getAbsoluteAdapterPosition();
            final int toPos = target.getAbsoluteAdapterPosition();
            adapter.notifyItemMoved(fromPos, toPos);

            final int totalNumOfToDo = adapter.getItemCount();
            final int reversedFromPos = totalNumOfToDo - fromPos - 1;
            final int reversedToPos = totalNumOfToDo - toPos - 1;

            itemManagementViewModel.updateToDoList(reversedFromPos, reversedToPos);

            return true;
        }

        @Override
        public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
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
                ((MainActivity)getActivity()).updateHomeFragmentFromItemManagement();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        itemManagementViewModel = new ViewModelProvider(this).get(ItemManagementViewModel.class);
        itemManagementViewModel.loadToDoList(tabId);
        itemManagementViewModel.loadTabs();

        recyclerView = binding.itemManagementRecyclerView;

        binding.itemManagementDeleteTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemManagementViewModel.deleteSelectedToDo();
            }
        });

        binding.itemManagementMoveTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToDoDialog dialog = new MoveToDoDialog(itemManagementViewModel.getTabList());
                dialog.setListener(dialogClickListener);
                dialog.show(getChildFragmentManager(), null);
            }
        });

        setUpRecyclerView();
        observeViewModel();
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemManagementAdapter(new ArrayList<ToDo>());
        adapter.setListener(new ItemManagementAdapter.Listener() {
            @Override
            public void onSortBtnClick(ItemManagementAdapter.ViewHolder viewHolder) {
                startDragging(viewHolder);
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
                adapter.updateToDos(toDos);
            }
        });
    }

    private void startDragging(ItemManagementAdapter.ViewHolder viewHolder){
        helper.startDrag(viewHolder);
    }

}