package com.example.todo.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.todo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemEditFragment extends Fragment {

    private int position;

    public ItemEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editText = view.findViewById(R.id.edit_editText);

        if(getArguments() != null){
            String text = ItemEditFragmentArgs.fromBundle(getArguments()).getEditText();
            editText.setText(text);

            position = ItemEditFragmentArgs.fromBundle(getArguments()).getPosition();
        }

        Button button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
