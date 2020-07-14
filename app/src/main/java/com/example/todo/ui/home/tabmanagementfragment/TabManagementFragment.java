package com.example.todo.ui.home.tabmanagementfragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.todo.R;
import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabManagementFragment extends Fragment {

    private static final String TAG = "TabManagementFragment";

    private TabManagementViewModel viewModel;

    private TabManagementAdapter adapter = new TabManagementAdapter(new ArrayList<Tab>());

    private TabManagementItemCallBack callBack;

    private ItemTouchHelper helper;

    private LinearLayoutManager linearLayoutManager;

    // ui components
    private RecyclerView recyclerView;

    public class TabManagementItemCallBack extends ItemTouchHelper.SimpleCallback {

        private Drawable icon;
        private ColorDrawable background;

        public TabManagementItemCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
            icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            final int fromPos = viewHolder.getAbsoluteAdapterPosition();
            final int toPos = target.getAbsoluteAdapterPosition();
            adapter.notifyItemMoved(fromPos, toPos);
            return true;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            if(direction == ItemTouchHelper.LEFT){
//                adapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
//            }
        }

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
        public boolean isLongPressDragEnabled() {
            return false;
        }

//        @Override
//        public boolean isItemViewSwipeEnabled() {
//            return false;
//        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if(dX < 0){

                itemView.setTranslationX(dX/7);
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                Log.d(TAG, "dx == " + dX);

                if(dX <= -100f){
                    dX = -100f;
                }


                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            }
            else{
                background.setBounds(0,0,0,0);
            }
            background.draw(c);
            icon.draw(c);
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
        viewModel.updateTabList();

        recyclerView = view.findViewById(R.id.tab_manage_recyclerView);

        linearLayoutManager = new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new TabManagementAdapter.Listener() {
            @Override
            public void onClick(TabManagementAdapter.ViewHolder viewHolder) {
                startDragging(viewHolder);
            }

            @Override
            public void onSwipeDeleteBack(int position) {
                Log.d(TAG, "onSwipeDeleteBack pos = " + position);
                TabManagementAdapter.ViewHolder viewHolder = adapter.getViewByPosition(position);
//                float x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, recyclerView.getResources().getDisplayMetrics());
                viewHolder.linearLayout.animate().translationX(0).setDuration(300).start();
            }
        });


        callBack = new TabManagementItemCallBack(
                ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT);
        helper = new ItemTouchHelper(callBack);
        helper.attachToRecyclerView(recyclerView);

        observeViewModel();
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
}