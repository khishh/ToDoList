package com.example.todo.ui.home.homefragment;

import android.os.Bundle;
import android.util.Log;

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
 *
 * 1. Manages all necessary data to show each Tab
 * 2. Through Listener interface, this class will be notified when FABs on HomeFragment are clicked and
 * this class will prompt ItemFragment to make some actions.
 *
 */
public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = HomeCollectionPagerAdapter.class.getSimpleName();

    // List of Fragment instances created
    private final List<Fragment> mFragmentList = new ArrayList<>();

    // List of Tab's title
    private final List<String> mFragmentTitleList = new ArrayList<>();

    // List of Tab's Id
    private final List<Integer> mFragmentTabIdList = new ArrayList<>();

    private KeyBoardVisibilityListener keyBoardVisibilityListener;

    public void setKeyBoardVisibilityListener(KeyBoardVisibilityListener keyBoardVisibilityListener) {
        this.keyBoardVisibilityListener = keyBoardVisibilityListener;
    }

    public HomeCollectionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    /**
     * Update PagerAdapter's data
     * LiveData object in HomeFragment will notify whenever any changes to the data will occur
     */
    public void updatePagerAdapter(List<Integer> newTabIds, List<String> newTabTitles){

        // clean up the previous lists
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
        for(int i = 0; i < newTabIds.size(); i++){
            ItemFragment fragment = new ItemFragment(newTabIds.get(i));
            fragment.setListener(new ItemFragment.Listener() {
                @Override
                public void keyboardVisibilityChange(boolean willBeShown) {
                    if(keyBoardVisibilityListener != null){
                        keyBoardVisibilityListener.keyboardVisibilityChange(willBeShown);
                    }
                }
            });

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

        // pass tabId to the fragment to show To-Do items belong to Tab which has that tabId
        bundle.putInt(ItemFragment.ARG_OBJECT, tabId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * Keep the record of Fragment instances, tabIds, and tab's titles.
     */
    public void addFragment(Fragment fragment, String title, int tabId,  int position) {
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, title);
        mFragmentTabIdList.add(position, tabId);
    }

    /**
     * Order the current shown ItemFragment to hide the user input fields
     */
    public void closeUserInput(int position){
        if(position < getCount()) {
            Fragment targetFragment = mFragmentList.get(position);
            ((ItemFragment) targetFragment).hideUserInput();
        }
    }

    /**
     * Order the current shown ItemFragment to show the user input field for adding a new To-Do
     */
    public void addNewBtnClicked(int position){
        Log.e(TAG, "position clicked == " + position);
        Fragment targetFragment = mFragmentList.get(position);
        ((ItemFragment)targetFragment).showAddNewItemInput();
    }

    /**
     * Order the current shown ItemFragment to delete completed To-Dos
     */
    public void deleteButtonClicked(int position){
        Fragment targetFragment = mFragmentList.get(position);
        ((ItemFragment)targetFragment).deleteAllDoneItems();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}