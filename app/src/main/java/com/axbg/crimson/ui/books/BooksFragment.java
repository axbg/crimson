package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentBooksBinding;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.ui.books.adapter.BookRecyclerViewAdapter;
import com.axbg.crimson.utility.UIHelper;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BooksFragment extends androidx.fragment.app.Fragment {
    private BooksViewModel booksViewModel;
    private ShimmerRecyclerView shimmerLayout;
    private BookRecyclerViewAdapter booksAdapter;
    private FragmentBooksBinding binding;

    private List<BookEntity> books = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);

        binding = FragmentBooksBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindLayout();
    }

    private void bindLayout() {
        bindAddBookButton();
        bindShimmer();
        bindRecyclerView(books);

        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(), liveBooks -> {
            shimmerLayout.hideShimmerAdapter();
            refreshBooksAdapter(liveBooks);
            toggleQuoteMenuItem(liveBooks.isEmpty());
            UIHelper.toggleView(liveBooks.isEmpty(), R.id.fragment_books_empty_list, requireActivity());
        });
    }

    private void bindAddBookButton() {
        FloatingActionButton addBookFab = binding.fragmentBooksAdd;
        addBookFab.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);
            nav.navigate(BooksFragmentDirections.createBookAction());
        });
    }

    private void bindShimmer() {
        shimmerLayout = binding.fragmentBooksShimmer;
        shimmerLayout.showShimmerAdapter();
    }

    private void bindRecyclerView(List<BookEntity> books) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);

        RecyclerView recyclerView = binding.fragmentBooksRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        booksAdapter = new BookRecyclerViewAdapter(books, requireActivity(), getOnBookSelectedListener());

        recyclerView.setAdapter(booksAdapter);
    }

    private void toggleQuoteMenuItem(boolean isEmpty) {
        BottomNavigationView navView = requireActivity().findViewById(R.id.activity_landing_nav_view);

        if (navView != null) {
            MenuItem quoteMenu = navView.getMenu().getItem(0);
            ImageView addQuotesImage = binding.fragmentBooksAddQuotes;

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

    private BiConsumer<FragmentActivity, BookEntity> getOnBookSelectedListener() {
        return (fragmentActivity, book) -> {
            NavController nav = Navigation.findNavController(fragmentActivity, R.id.activity_landing_nav_host_fragment);

            BooksFragmentDirections.CreateBookAction action = BooksFragmentDirections.createBookAction();
            action.setBookId(book.getId());
            action.setCreate(false);

            nav.navigate(action);
        };
    }
}