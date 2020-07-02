package com.example.todo.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.todo.R;
import com.google.android.material.tabs.TabLayout;


public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;

    private HomeCollectionPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "HomeFragment created");
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
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
        viewPager.setAdapter(pagerAdapter);
    }

    public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter{


        public HomeCollectionPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ItemFragment.ARG_OBJECT, position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return "List " + (position + 1);
        }
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment destroyed");
    }
}
