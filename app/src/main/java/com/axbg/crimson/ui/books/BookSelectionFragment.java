package com.axbg.crimson.ui.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentBookSelectionBinding;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.ui.books.adapter.BookRecyclerViewAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class BookSelectionFragment extends Fragment {
    private BooksViewModel booksViewModel;
    private ShimmerRecyclerView shimmerLayout;
    private BookRecyclerViewAdapter booksAdapter;
    private FragmentBookSelectionBinding binding;

    private List<BookEntity> books = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        binding = FragmentBookSelectionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindLayout();
    }

    private void bindLayout() {
        bindShimmer();
        bindRecyclerView(books);

        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(), liveBooks -> {
            shimmerLayout.hideShimmerAdapter();
            refreshBooksAdapter(liveBooks);
        });
    }

    private void bindRecyclerView(List<BookEntity> books) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);

        RecyclerView recyclerView = binding.fragmentBookSelectionRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        booksAdapter = new BookRecyclerViewAdapter(books, requireActivity(), getOnBookSelectedListener());

        recyclerView.setAdapter(booksAdapter);
    }

    private void bindShimmer() {
        shimmerLayout = binding.fragmentFragmentBookSelectionShimmer;
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

    private BiConsumer<FragmentActivity, BookEntity> getOnBookSelectedListener() {
        return (fragmentActivity, book) -> {
            NavController nav = Navigation.findNavController(fragmentActivity, R.id.activity_landing_nav_host_fragment);

            SavedStateHandle handle = Objects.requireNonNull(nav.getPreviousBackStackEntry())
                    .getSavedStateHandle();
            handle.set("BOOK_ID", book.getId());
            handle.set("BOOK_TITLE", book.getTitle());

            nav.popBackStack();
        };
    }
}