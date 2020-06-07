package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BooksFragment extends androidx.fragment.app.Fragment {
    private List<BookEntity> books;
    private BooksAdapter booksAdapter;
    private BooksViewModel booksViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_books, container, false);

        this.books = booksViewModel.getBooks();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindAddBookButton();
        bindGridView();
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

    private void bindGridView() {
        booksAdapter = new BooksAdapter(books, R.layout.adapter_books, requireContext());
        GridView booksGridView = requireView().findViewById(R.id.books_grid_view);
        booksGridView.setAdapter(booksAdapter);
    }
}