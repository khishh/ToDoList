package com.example.todo.ui.home.homefragment;

import android.content.Context;
import android.os.Bundle;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.example.todo.R;
import com.example.todo.model.Tab;
import com.example.todo.ui.home.itemfragment.ItemFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;

    private HomeCollectionPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private Toolbar toolbar;

    private TabLayout tabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "HomeFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment onCreateView");

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment onViewCreated");


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.initializeData();


        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

//        Log.d(TAG, "adapter created");
        pagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                hideKeyboard(view);
            }

            // when the tab page changed, close the keyboard
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        viewPager.setAdapter(pagerAdapter);

        observeViewModel();

    }



    private void observeViewModel() {
        homeViewModel.getTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {

                Log.d(TAG, "HomeFragment onChanged");
                Log.d(TAG, tabs.toString());
                pagerAdapter.updatePagerAdapter();

            }
        });
    }


    private void initializeTabFragments(){

        Log.d(TAG, "initializeTabFragments called");
        for(int i = 0; i < homeViewModel.getTabListSize(); i++){

            ItemFragment fragment = new ItemFragment(homeViewModel.getTabIdAtPosition(i));
            pagerAdapter.addFragment(fragment,
                    homeViewModel.getTabTitleWithPosition(i),
                    homeViewModel.getTabIdAtPosition(i),
                    i);
        }
    }

    public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Integer> mFragmentTabIdList = new ArrayList<>();

        public HomeCollectionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }



        public void updatePagerAdapter(){

//            Log.d(TAG, "UPDATED Adapter " + tabs.toString());
            mFragmentTitleList.clear();
            mFragmentTabIdList.clear();
            mFragmentList.clear();
            initializeTabFragments();
//            Log.d(TAG, "Id List == " + mFragmentTabIdList.toString());
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);
            int tabId = homeViewModel.getTabIdAtPosition(position);
//            Log.d(TAG, "Pager position == " + position + " its tabId == " + tabId);
            Bundle bundle = new Bundle();
            bundle.putInt(ItemFragment.ARG_OBJECT, tabId);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, int tabId,  int position) {
            mFragmentList.add(position, fragment);
            mFragmentTitleList.add(position, title);
            mFragmentTabIdList.add(position, tabId);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        // called after notifyDataSetChanged() for all fragments
//        @Override
//        public int getItemPosition(@NonNull Object object) {
//            ItemFragment fragment = (ItemFragment)object;
//            int tabId = fragment.getTabId();
//            int position = mFragmentTabIdList.indexOf(tabId);
////
////            if(position >= 0){
////                Log.d(TAG, "getItemPosition tabId: " + tabId + " position: " + position);
////                return position;
////            }
////            else{
////                Log.d(TAG, "getItemPosition tabId: " + tabId + " position None" );
////                return POSITION_NONE;
////            }
//            Log.d(TAG, "getItemPosition tabId: " + tabId + " position: " + position);
//            return  POSITION_NONE;
//        }
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


        switch (item.getItemId()){

            case R.id.add_newTab:

                hideKeyboard(getView());
                Navigation.findNavController(getView()).navigate(R.id.MoveToTabManagement);

                break;
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
}
