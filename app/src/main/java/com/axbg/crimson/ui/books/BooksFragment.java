package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class BooksFragment extends androidx.fragment.app.Fragment {
    private BooksViewModel booksViewModel;
    private ShimmerRecyclerView shimmerLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindAddBookButton();
        bindShimmer();

        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(), books -> {
            shimmerLayout.hideShimmerAdapter();
            bindGridView(books);
        });
    }

    private void bindAddBookButton() {
        FloatingActionButton addBookFab = requireView().findViewById(R.id.add_book_fab);
        addBookFab.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            nav.navigate(R.id.books_detail);
        });
    }

    private void bindGridView(List<BookEntity> books) {
        try {
            BooksAdapter booksAdapter = new BooksAdapter(books, R.layout.adapter_books, requireContext());
            GridView booksGridView = requireView().findViewById(R.id.books_grid_view);
            booksGridView.setAdapter(booksAdapter);
        } catch (Exception ignored) {
        }
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_books_shimmer);
        shimmerLayout.showShimmerAdapter();
    }
}