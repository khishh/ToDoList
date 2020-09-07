package com.example.todo;

import android.os.Bundle;
import android.util.Log;

import com.example.todo.ui.home.homefragment.HomeFragment;
import com.example.todo.ui.home.itemmanagementfragment.ItemManagementFragment;
import com.example.todo.ui.home.tabmanagementfragment.TabManagementFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 *  MainActivity
 *  Simple activity holding a FrameLayout
 *  Only Managing fragment translation in this activity.
 *  Initial fragment shown is HomeFragment.
 *
 *  Possible Fragments in FrameLayout : {HomeFragment, ItemManagementFragment, TabManagementFragment}
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String TAG_HOME_FRAGMENT = HomeFragment.class.getSimpleName();
    private static final String TAG_ITEM_MANAGEMENT_FRAGMENT = ItemManagementFragment.class.getSimpleName();
    private static final String TAG_TAB_MANAGEMENT_FRAGMENT = TabManagementFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity created");
        createHomeFragment();
    }

    /**
     * show HomeFragment (the launching fragment)
     * only called when the app starts
     */
    public void createHomeFragment(){
        HomeFragment fragment = HomeFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment).commit();
    }

    /**
     * Display TabManagementFragment
     * A fragment where users can add/delete/sort/edit Tab
     */
    public void createTabManagementFragment(){
        TabManagementFragment fragment = TabManagementFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_down, R.anim.slide_to_up, R.anim.slide_from_top, R.anim.slide_to_down)
                .add(R.id.container, fragment)
                .addToBackStack(TAG_TAB_MANAGEMENT_FRAGMENT)
                .commit();
    }

    /**
     * Display ItemManagementFragment
     * A fragment where users can add/delete/sort To-Do items
     */
    public void createItemManagementFragment(int tabId){
        ItemManagementFragment fragment = ItemManagementFragment.newInstance(tabId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left)
                .add(R.id.container, fragment)
                .addToBackStack(TAG_ITEM_MANAGEMENT_FRAGMENT)
                .commit();
    }

    /**
     * Pop the fragment on top of the BackStack
     * -> take users back to the previous screen
     */
    public void popOffFragment(){
        getSupportFragmentManager().popBackStack();
    }

    /**
     * After popping off the fragment on top of the BackStack,
     * replace a new HomeFragment with the old HomeFragment
     * only called when Tab was deleted in the TabManagementFragment as a pager needs to be refreshed
     * after removing one of its Tabs
     */
    public void popOffFragmentAndResetHomeFragment(){
        popOffFragment();
        HomeFragment fragment = HomeFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment).commit();
    }
}
