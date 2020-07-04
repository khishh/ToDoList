package com.example.todo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.example.todo.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private NavController navController;

    private ConstraintLayout rootView;

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity created");

        rootView = findViewById(R.id.main_container);
        setKeyBoardOpenListener();

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }



    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout)null);
    }

    //    detect if screen shows keyboard
    //    https://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
    private void setKeyBoardOpenListener(){
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, String.valueOf(rootView.getRootView().getHeight()) + " " + rootView.getHeight());
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if(heightDiff > Util.dpToPx(rootView.getContext(), 200)){
                    navView.setVisibility(View.GONE);
                }
                else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            navView.setVisibility(View.VISIBLE);
                        }
                    }, 50);

                }
            }
        });
    }
}
