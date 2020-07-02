package com.example.todo.ui.home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private int position;

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
            position = bundle.getInt(ARG_OBJECT, 0);

        recyclerView = view.findViewById(R.id.list_recycler_view);

        LinearLayoutManagerWithSmoothScroller linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ItemAdapter(this, new ArrayList<ToDo>());
        adapter.setListener(new ItemAdapter.Listener() {
            @Override
            public void onClick(final int position) {
                Log.d(TAG, "position = " + position);

                // keep the position of item clicked and will be used for smoothScroll in setVisibilityListener
                positionItem = position;

                if(linearLayout.getVisibility() == View.GONE){
                    showKeyboard(linearLayout);

//                    linearLayout.setVisibility(View.VISIBLE);
//                    editText.setVisibility(View.VISIBLE);
//                    updateBtn.setVisibility(View.VISIBLE);

                }
                else{
//                    linearLayout.setVisibility(View.GONE);
//                    editText.setVisibility(View.GONE);
//                    updateBtn.setVisibility(View.GONE);
                    hideKeyboard(linearLayout);
                }
            }
        });
        recyclerView.setAdapter(adapter);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // for test
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.setToDoList(position);
        observeViewModel();

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

//    private void setUpUI(View view){
//
//        Log.d(TAG, "View = " + view.getClass().toString());
//
//        // if view touched is the container of EditText and Buttons, skip setting onTouchListener
//        if(view.getId() == R.id.item_linear_layout || view.getId() == R.id.user_input_linear_layout
//        || view.getId() == R.id.list_recycler_view){
//            Log.d(TAG, "Found seeking linear layout " + view.getId());
//        }
//        else if( view.getId() != R.id.item_linear_layout &&
//                 view.getId() != R.id.item_image &&
//                 view.getId() != R.id.user_input_edit_text &&
//                 view.getId() != R.id.update_btn){
//            view.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    hideKeyboard(v);
//                    hideEditDialog();
//                    return false;
//                }
//            });
//        }
//
//        if(view instanceof ViewGroup){
//            Log.d(TAG, "innerView = " + ((ViewGroup)view).getChildCount());
//            for(int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
//                View innerView = ((ViewGroup)view).getChildAt(i);
//                Log.d(TAG, "innerView = " + innerView.getClass().toString());
//                setUpUI(innerView);
//            }
//        }
//    }

    private void showKeyboard(View view){
        // how to show keyboard programmatically
        // https://stackoverflow.com/questions/39228245/how-to-show-soft-keyboard-perfectly-in-fragment-in-android
        InputMethodManager inputMethodManager = (InputMethodManager)(view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        if(inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//        recyclerView.smoothScrollToPosition(2);
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }



//    private void hideEditDialog(){
//        Log.d(TAG, String.valueOf("Before " +linearLayout.getVisibility()));
//        if(linearLayout.getVisibility() == View.VISIBLE){
//            Log.d(TAG, "pass here");
//            linearLayout.setVisibility(View.GONE);
//            updateBtn.setVisibility(View.GONE);
//            editText.setVisibility(View.GONE);
//        }
//        Log.d(TAG, "after " + String.valueOf(linearLayout.getVisibility()));
//    }

    // control the visibility of the user input area depending on the current size of the screen
    // --> if the height diff is more than 200(assuming that soft input is open already), so open the input area
    // --> else key board is hidden so set the input area gone
    private void setVisibilityListener(){
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, frameLayout.getRootView().getHeight() + " " + frameLayout.getHeight());
                int heightDiff = frameLayout.getRootView().getHeight() - frameLayout.getHeight();
                if (heightDiff < Util.dpToPx(frameLayout.getContext(), 200)) {
                    linearLayout.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    updateBtn.setVisibility(View.GONE);

                }
                else{
                    linearLayout.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.VISIBLE);

                    recyclerView.smoothScrollToPosition(positionItem);
                }
            }
        });
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
}
