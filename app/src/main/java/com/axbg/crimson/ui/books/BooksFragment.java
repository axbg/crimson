package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;

public class BooksFragment extends androidx.fragment.app.Fragment {

    private BooksViewModel booksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_books, container, false);

        final TextView textView = root.findViewById(R.id.text_home);

        booksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}
