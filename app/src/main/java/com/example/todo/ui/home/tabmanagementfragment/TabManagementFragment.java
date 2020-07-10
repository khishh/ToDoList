package com.example.todo.ui.home.tabmanagementfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.R;
import com.example.todo.model.Tab;

import java.util.ArrayList;
import java.util.List;

public class TabManagementFragment extends Fragment {

    private TabManagementViewModel viewModel;

    private TabManagementAdapter adapter = new TabManagementAdapter(new ArrayList<Tab>());

    // ui components
    private RecyclerView recyclerView;


    public TabManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(TabManagementViewModel.class);
        viewModel.updateTabList();

        recyclerView = view.findViewById(R.id.tab_manage_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel(){
        viewModel.getmTabList().observe(getViewLifecycleOwner(), new Observer<List<Tab>>() {
            @Override
            public void onChanged(List<Tab> tabs) {
                adapter.updateTabList(tabs);
            }
        });
    }
}