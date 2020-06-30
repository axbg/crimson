package com.axbg.crimson.ui.books;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.axbg.crimson.R;
import com.axbg.crimson.network.HttpCall;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.axbg.crimson.network.HttpMethods.GET_METHOD;


public class CoverFragment extends Fragment {
    private ShimmerRecyclerView shimmerLayout;
    private List<OpenLibraryBook> books = new ArrayList<>();
    private OpenLibraryBooksAdapter booksAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindSearchButton();
        bindTakePhotoButton();
        bindShimmer();
        bindGridView(books);
    }

    private void bindSearchButton() {
        TextInputEditText searchQueryInput = requireView().findViewById(R.id.cover_search_input_text);
        Button searchButton = requireView().findViewById(R.id.cover_search_button);
        searchButton.setOnClickListener(v -> {
            String searchQuery = searchQueryInput.getText() != null ? searchQueryInput.getText().toString()
                    : "";

            if (!searchQuery.isEmpty()) {
                @SuppressLint("StaticFieldLeak") HttpCall httpCall = new HttpCall() {
                    @Override
                    protected void onPostExecute(String s) {
                        refreshBooksAdapter(OpenLibraryBook.fromJson(s));
                        hideShimmer();
                    }
                };

                showShimmer();
                refreshBooksAdapter(null);
                httpCall.execute(GET_METHOD, NetworkUtil.buildSearchUrl(searchQuery));
            }
        });
    }

    private void refreshBooksAdapter(List<OpenLibraryBook> newBooks) {
        if (books != null && booksAdapter != null) {
            books.clear();

            if (newBooks != null) {
                books.addAll(newBooks);
            }

            booksAdapter.notifyDataSetChanged();
        }
    }

    private void bindTakePhotoButton() {
        FloatingActionButton addBookFab = requireView().findViewById(R.id.cover_camera_fab);
        addBookFab.setOnClickListener(v -> {
            // open Take Photo Activity
        });
    }

    private void bindGridView(List<OpenLibraryBook> books) {
        try {
            booksAdapter = new OpenLibraryBooksAdapter(books, R.layout.adapter_books, requireContext());

            GridView booksGridView = requireView().findViewById(R.id.cover_grid_view);
            booksGridView.setOnItemClickListener((parent, view, position, id) -> {
                NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Objects.requireNonNull(nav.getPreviousBackStackEntry()).
                        getSavedStateHandle().set("OPEN_BOOK", books.get(position));
                nav.popBackStack();
            });
            booksGridView.setAdapter(booksAdapter);
        } catch (Exception ignored) {
        }
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_cover_shimmer);
    }

    private void showShimmer() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.showShimmerAdapter();
    }

    private void hideShimmer() {
        shimmerLayout.hideShimmerAdapter();
    }
}
