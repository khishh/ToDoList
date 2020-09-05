package com.example.todo.ui.home.homefragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.databinding.FragmentHomeBinding;
import com.example.todo.model.Tab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;



public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private HomeCollectionPagerAdapter pagerAdapter;

    private ConstraintLayout constraintLayout;
    private FloatingActionButton addBtn;
    private FloatingActionButton deleteBtn;

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            hideKeyboard(requireView());
        }

        // when the tab page changed, close the keyboard
        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    /**
     * return HomeFragment instance
     */
    public static HomeFragment getInstance(){
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "HomeFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "HomeFragment onCreateView");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        constraintLayout = binding.homeContainer;
        addBtn = binding.btnAddTodo;
        deleteBtn = binding.btnDeleteTodo;

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter.addNewBtnClicked(binding.pager.getCurrentItem());
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter.deleteButtonClicked(binding.pager.getCurrentItem());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment onViewCreated");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.initializeData();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        setUpViewPager();
        observeViewModel();
    }

    private void setUpViewPager(){
        pagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.setListener(new HomeCollectionPagerAdapter.Listener() {
            @Override
            public void keyboardVisibilityChange(boolean willBeShown) {
                if(willBeShown){
                    addBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                }
                else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addBtn.setVisibility(View.VISIBLE);
                            deleteBtn.setVisibility(View.VISIBLE);
                        }
                    }, 350);

                }
            }
        });
        binding.pager.addOnPageChangeListener(listener);
        binding.pager.setAdapter(pagerAdapter);
    }


    private void observeViewModel() {
        homeViewModel.getmTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                Log.e(TAG, "Tab onChanged");
                Log.e(TAG, tabs.toString());

                List<Integer> newTabIds = new ArrayList<>();
                List<String> newTabTitles = new ArrayList<>();
                for(Tab tab : tabs){
                    newTabIds.add(tab.getTabId());
                    newTabTitles.add(tab.getTabTitle());
                }

                pagerAdapter.updatePagerAdapter(newTabIds, newTabTitles);
            }
        });
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int curPage = binding.pager.getCurrentItem();
        Log.e(TAG, "curPage == " + curPage);
        pagerAdapter.closeUserInput(curPage);

        switch (item.getItemId()){

            case R.id.add_newTab:
                hideKeyboard(requireView());

                ((MainActivity)requireActivity()).createTabManagementFragment();
                break;

            case R.id.edit_todo:
                hideKeyboard(requireView());
                int currentTabIndex = binding.pager.getCurrentItem();
                Log.d(TAG, "currentTabIndex == " + currentTabIndex);
                ((MainActivity)requireActivity()).createItemManagementFragment(homeViewModel.getTabIdAtPosition(currentTabIndex));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        // save the last shown index of Pager
        Log.d(TAG, "HomeFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment destroyed");
    }

    private void setKeyboardListener(){
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = constraintLayout.getRootView().getHeight() - constraintLayout.getHeight();
                Log.e(TAG, "HeightDiff == " + heightDiff);
                Log.e(TAG, "parent height == " + constraintLayout.getRootView().getHeight() + " container = " + constraintLayout.getHeight());

                if(heightDiff > constraintLayout.getRootView().getHeight()/4){

                    Log.e(TAG, "onGlobalLayout -- Gone passed");

                    addBtn.setVisibility(View.GONE);
                    deleteBtn.setVisibility(View.GONE);
                }
                else{
                    addBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
