package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.ui.books.adapter.BooksAdapter;
import com.axbg.crimson.utility.UIHelper;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends androidx.fragment.app.Fragment {
    private BooksViewModel booksViewModel;
    private ShimmerRecyclerView shimmerLayout;
    private BooksAdapter booksAdapter;

    private List<BookEntity> books = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindLayout();
    }

    private void bindLayout() {
        bindAddBookButton();
        bindShimmer();
        bindGridView(books);

        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(), liveBooks -> {
            shimmerLayout.hideShimmerAdapter();
            refreshBooksAdapter(liveBooks);
            toggleQuoteMenuItem(liveBooks.isEmpty());
            UIHelper.toggleView(liveBooks.isEmpty(), R.id.fragment_books_empty_list, requireActivity());
        });
    }

    private void bindAddBookButton() {
        FloatingActionButton addBookFab = requireView().findViewById(R.id.fragment_books_add);
        addBookFab.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);
            nav.navigate(BooksFragmentDirections.createBookAction());
        });
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_books_shimmer);
        shimmerLayout.showShimmerAdapter();
    }

    private void bindGridView(List<BookEntity> books) {
        try {
            booksAdapter = new BooksAdapter(books, R.layout.adapter_books, requireContext());

            GridView booksGridView = requireView().findViewById(R.id.fragment_books_grid_view);

            booksGridView.setOnItemClickListener((parent, view, position, id) -> {
                NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);

                BooksFragmentDirections.CreateBookAction action = BooksFragmentDirections.createBookAction();
                action.setBookId(books.get(position).getId());
                action.setCreate(false);

                nav.navigate(action);
            });

            booksGridView.setAdapter(booksAdapter);
        } catch (
                Exception ignored) {
        }

    }

    private void toggleQuoteMenuItem(boolean isEmpty) {
        BottomNavigationView navView = requireActivity().findViewById(R.id.activity_landing_nav_view);

        if (navView != null) {
            MenuItem quoteMenu = navView.getMenu().getItem(0);
            ImageView addQuotesImage = requireActivity().findViewById(R.id.fragment_books_add_quotes);

            if (isEmpty && quoteMenu.isEnabled()) {
                navView.getMenu().getItem(0).setEnabled(false);
            } else if (!isEmpty && !quoteMenu.isEnabled()) {
                navView.getMenu().getItem(0).setEnabled(true);
                addQuotesImage.setVisibility(View.VISIBLE);
            } else if (addQuotesImage.getVisibility() == View.VISIBLE) {
                addQuotesImage.setVisibility(View.GONE);
            }
        }
    }

    private void refreshBooksAdapter(List<BookEntity> newBooks) {
        if (books != null && booksAdapter != null) {
            books.clear();

            if (newBooks != null) {
                books.addAll(newBooks);
            }

            booksAdapter.notifyDataSetChanged();
        }
    }
}