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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.example.todo.R;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment created");
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        pagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager());
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideKeyboard(view);
            }

            @Override
            public void onPageSelected(int position) {
                hideKeyboard(view);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // when the tab page changed, close the keyboard
                 hideKeyboard(view);
            }
        });


        // test

        initializeTabFragments();

//        pagerAdapter.addFragment(new ItemFragment(), "Hello", 0);
        viewPager.setAdapter(pagerAdapter);

    }

    private void initializeTabFragments(){

        for(int i = 0; i < homeViewModel.getTabCount().getValue(); i++){
            pagerAdapter.addFragment(new ItemFragment(), homeViewModel.getTabTitles().getValue().get(i), i);
        }

    }

    public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public HomeCollectionPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
//            Fragment fragment = new ItemFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt(ItemFragment.ARG_OBJECT, position);
//            fragment.setArguments(bundle);
//
//            mFragmentList.add(position, fragment);
//            mFragmentTitleList.add(position, String.valueOf(position+1));

            Fragment fragment = mFragmentList.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt(ItemFragment.ARG_OBJECT, position);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, int position) {
            mFragmentList.add(position, fragment);
            mFragmentTitleList.add(position, title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
//            return "List " + (position + 1);
            return mFragmentTitleList.get(position);
        }
    }

    private void hideKeyboard(View view){
        Log.d(TAG, "keyBoardCloseRequest called");
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
//        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment destroyed");
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
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
//                ToDoCollection.getInstance().incrementSizeOfCollection();

                // temporary title
//                String newTabTitle = "New";
//                pagerAdapter.addFragment(new ItemFragment(), newTabTitle, pagerAdapter.getCount());
//                homeViewModel.incrementTabSize(newTabTitle);
//                pagerAdapter.notifyDataSetChanged();

                hideKeyboard(getView());
//                HomeFragmentDirections.ActionNavigationHomeToItemEditFragment  action = HomeFragmentDirections.actionNavigationHomeToItemEditFragment();
                Navigation.findNavController(this.getView()).navigate(R.id.MoveToTabManagement);

                break;

        }

        return super.onOptionsItemSelected(item);
    }


}
