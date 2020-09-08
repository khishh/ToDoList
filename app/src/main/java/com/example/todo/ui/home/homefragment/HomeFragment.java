package com.example.todo.ui.home.homefragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.todo.util.KeyBoardVisibilityListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * HomeFragment
 *
 * 1. HomeFragment displays the TabLayout embedded inside ViewPager, toolbar and 2 FABs, adding To-Do button and
 * deleting completed To-Do button.
 *
 * 2. HomeFragment will display one ItemFragment inside TabLayout, where displays all To-Do items belong to one Tab.
 * Users can swipe left and right to move to other Tabs.
 *
 * 3. This Fragment will control the user clicks onto 2 FABs and notify its PagerAdapter the event. And the PagerAdapter
 * will notify ItemFragment to handle the event.
 *
 */

public class HomeFragment extends Fragment
    implements KeyBoardVisibilityListener {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private HomeCollectionPagerAdapter pagerAdapter;

    /**
     * UI components
     */
    private ConstraintLayout constraintLayout;
    private FloatingActionButton addBtn;
    private FloatingActionButton deleteBtn;

    /**
     * Listener
     */
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

        setUpUIComponents();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment onViewCreated");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // fetching Tab data from database
        homeViewModel.loadTabs();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        setHasOptionsMenu(true);

        setUpViewPager();
        observeViewModel();
    }

    /**
     * refer to UI components and attach listeners to them
     */
    private void setUpUIComponents(){
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
    }

    /**
     * Set up ViewPager & ViewPagerAdapter and attach listeners to ViewPagerAdapter
     */
    private void setUpViewPager(){
        pagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.setKeyBoardVisibilityListener(this);
        binding.pager.addOnPageChangeListener(listener);
        binding.pager.setAdapter(pagerAdapter);
    }

    /**
     * Observe any changes in Tab Table in database and pass new data to ViewPagerAdapter
     * Pass the list of TabIds (uuid for Tab) and the list of TabTitles to adapter
     */
    private void observeViewModel() {
        homeViewModel.getmTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                Log.e(TAG, "Tab onChanged");

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

        // get the current shown page index and tell adapter ItemFragment to close user input field if
        // it is visible
        int curPageIndex = binding.pager.getCurrentItem();
        pagerAdapter.closeUserInput(curPageIndex);

        switch (item.getItemId()){

            case R.id.add_newTab:
                vibrate();
                ((MainActivity)requireActivity()).createTabManagementFragment();
                break;

            case R.id.edit_todo:
                vibrate();
                ((MainActivity)requireActivity()).createItemManagementFragment(homeViewModel.getTabIdAtPosition(curPageIndex));
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

    private void vibrate(){
        Vibrator vibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.EFFECT_TICK);
    }

    /**
     * KeyBoardVisibilityListener interface
     */
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
            }, 300);

        }
    }
}
