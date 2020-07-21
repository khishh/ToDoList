package com.example.todo.ui.home.homefragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.todo.ui.home.itemfragment.ItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * PagerAdapter class for TabLayout
 */
public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<Integer> mFragmentTabIdList = new ArrayList<>();

    public HomeCollectionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void updatePagerAdapter(List<Integer> newTabIds, List<String> newTabTitles){
        mFragmentTitleList.clear();
        mFragmentTabIdList.clear();
        mFragmentList.clear();

        initializeTabFragments(newTabIds, newTabTitles);
        notifyDataSetChanged();
    }


    /**
     * Method to create all ItemFragments and keep them in PagerAdapter class by calling
     * pagerAdapter.addFragment(fragment, title Of tab, position of tab)
     */
    private void initializeTabFragments(List<Integer> newTabIds, List<String> newTabTitles){
//        Log.d(TAG, "initializeTabFragments called");
        for(int i = 0; i < newTabIds.size(); i++){
            ItemFragment fragment = new ItemFragment(newTabIds.get(i));
            addFragment(fragment,
                    newTabTitles.get(i),
                    newTabIds.get(i),
                    i);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = mFragmentList.get(position);
        int tabId = mFragmentTabIdList.get(position);
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
}