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
 *  Only Managing fragment translation in this activity
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_HOME_FRAGMENT = "home_fragment";
    private static final String KEY_TAB_MANAGEMENT = "tab_management";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity created");

        createHomeFragment();
    }

    /**
     * show HomeFragment (default fragment)
     * only called when the app starts
     */
    public void createHomeFragment(){
        HomeFragment fragment = HomeFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment).commit();
    }

    /**
     * show TabManagementFragment
     */
    public void createTabManagementFragment(){
        TabManagementFragment fragment = TabManagementFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_from_down, R.anim.slide_to_up).replace(R.id.container, fragment).commit();
        ft.setCustomAnimations(R.anim.slide_from_down, R.anim.slide_to_up, R.anim.slide_from_top, R.anim.slide_to_down)
                .add(R.id.container, fragment)
                .addToBackStack(TabManagementFragment.class.getSimpleName())
                .commit();
    }

    public void createItemManagementFragment(int tabId){
        ItemManagementFragment fragment = ItemManagementFragment.newInstance(tabId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right, R.anim.slide_from_right, R.anim.slide_to_left)
                .add(R.id.container, fragment)
                .addToBackStack(ItemManagementFragment.class.getSimpleName())
                .commit();
    }

    public void popOffFragment(){
        getSupportFragmentManager().popBackStack();
    }

    /**
     * show HomeFragment called from other fragments to come back
     * using replace to show the up-to-date data
     */
    public void updateHomeFragmentFromTabManagement(){
        Log.d(TAG, "updateHomeFragment");
        HomeFragment fragment = HomeFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_top, R.anim.slide_to_down).replace(R.id.container, fragment).commit();
    }

    public void updateHomeFragmentFromItemManagement(){
        Log.d(TAG, "updateHomeFragment");
        HomeFragment fragment = HomeFragment.getInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left).replace(R.id.container, fragment).commit();
    }

    // for later use
//    //    detect if screen shows keyboard
//    //    https://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
//    private void setKeyBoardOpenListener(){
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Log.d(TAG, String.valueOf(rootView.getRootView().getHeight()) + " " + rootView.getHeight());
//                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
//                if(heightDiff > Util.dpToPx(rootView.getContext(), 200)){
//                    navView.setVisibility(View.GONE);
//                }
//                else{
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            navView.setVisibility(View.VISIBLE);
//                        }
//                    }, 50);
//
//                }
//            }
//        });
//    }
}
