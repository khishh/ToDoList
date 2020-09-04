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

    // List of Tab's titles
    private final List<Fragment> mFragmentList = new ArrayList<>();

    // List of Fragment instances created
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private final List<Integer> mFragmentTabIdList = new ArrayList<>();

    private Listener listener;

    interface Listener{
        void keyboardVisibilityChange(boolean willBeShown);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public HomeCollectionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    // LiveData object will notify whenever any changes to the data will occur
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
                    if(listener != null){
                        listener.keyboardVisibilityChange(willBeShown);
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

    public void closeUserInput(int position){
        Fragment targetFragment = mFragmentList.get(position);
        ((ItemFragment)targetFragment).hideUserInput();
    }

    public void addNewBtnClicked(int position){
        Fragment targetFragment = mFragmentList.get(position);
        ((ItemFragment)targetFragment).showAddNewItemInput();
    }

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