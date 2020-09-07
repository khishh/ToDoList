package com.example.todo.ui.home.itemmanagementfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class MoveToDoDialog extends DialogFragment {

    private final static String TAG = MoveToDoDialog.class.getSimpleName();

    List<Tab> tabs;

    MoveToDoDialogClickListener listener;

    public void setListener(MoveToDoDialogClickListener listener) {
        this.listener = listener;
    }

    public MoveToDoDialog(List<Tab> tabs){
        this.tabs = tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        List<String> tabTitles = new ArrayList<>();
        for(Tab tab : tabs){
            tabTitles.add(tab.getTabTitle());
        }
        String[] tabArray = tabTitles.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Click a tab where selected ToDo will be moved to")
                .setItems(tabArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null){
                            listener.onDialogClick(tabs.get(which).getTabId());
                            Log.d(TAG, "which: " + which + " id " + tabs.get(which).getTabId());
                        }
                    }
                });
        return builder.create();
    }
}
