package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.axbg.crimson.ui.quotes.QuotesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends androidx.fragment.app.Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_books, container, false);

        final TextView textView = root.findViewById(R.id.text_books);
        textView.setText("Books Fragment");

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindAddBookButton();
    }

    private void bindAddBookButton() {
        FloatingActionButton addBookFab = requireView().findViewById(R.id.add_book_fab);
        addBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open BookEditActivity
            }
        });
    }
}
