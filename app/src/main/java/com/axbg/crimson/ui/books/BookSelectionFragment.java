package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.ui.books.adapter.BooksAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookSelectionFragment extends Fragment {
    private BooksViewModel booksViewModel;
    private ShimmerRecyclerView shimmerLayout;
    private BooksAdapter booksAdapter;

    private List<BookEntity> books = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        return inflater.inflate(R.layout.fragment_book_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindShimmer();
        bindGridView(books);

        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(), liveBooks -> {
            shimmerLayout.hideShimmerAdapter();
            refreshBooksAdapter(liveBooks);
        });
    }

    private void bindGridView(List<BookEntity> books) {
        try {
            booksAdapter = new BooksAdapter(books, R.layout.adapter_books, requireContext());
            GridView booksGridView = requireView().findViewById(R.id.book_selection_grid_view);

            booksGridView.setOnItemClickListener(((parent, view, position, id) -> {
                NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                SavedStateHandle handle = Objects.requireNonNull(nav.getPreviousBackStackEntry())
                        .getSavedStateHandle();
                handle.set("BOOK_ID", books.get(position).getId());
                handle.set("BOOK_TITLE", books.get(position).getTitle());

                nav.popBackStack();
            }));

            booksGridView.setAdapter(booksAdapter);
        } catch (Exception ignored) {
        }
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_book_selection_shimmer);
        shimmerLayout.showShimmerAdapter();
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