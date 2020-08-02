package com.axbg.crimson.ui.books;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentCoverBinding;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.VolleyManager;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.axbg.crimson.ui.books.adapter.OpenBookRecyclerViewAdapter;
import com.axbg.crimson.utility.ImageProcessor;
import com.axbg.crimson.utility.UIHelper;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class CoverFragment extends Fragment {
    private ShimmerRecyclerView shimmerLayout;
    private FragmentCoverBinding binding;
    private OpenBookRecyclerViewAdapter booksAdapter;

    private int currentPage = 1;
    private boolean moreAvailable = true;
    private List<OpenLibraryBook> books = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCoverBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindLayout();
    }

    private void bindLayout() {
        bindSearchInput();
        bindSearchButton();
        bindTakePhotoButton();
        bindShimmer();
        bindRecyclerView();
    }

    private void bindSearchInput() {
        TextInputEditText searchInput = binding.fragmentCoverSearchInputText;
        searchInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                this.executeInitialSearch();
                return true;
            }

            return false;
        });
    }

    private void bindSearchButton() {
        Button searchButton = binding.fragmentCoverSearchButton;
        searchButton.setOnClickListener(v -> this.executeInitialSearch());
    }

    private void executeInitialSearch() {
        currentPage = 1;
        searchOpenLibrary(true);
    }

    private void searchOpenLibrary(boolean clear) {
        TextInputEditText searchQueryInput = binding.fragmentCoverSearchInputText;
        String searchQuery = searchQueryInput.getText() != null ? searchQueryInput.getText().toString() : "";

        if (!searchQuery.isEmpty()) {
            StringRequest request = new StringRequest(Request.Method.GET, NetworkUtil.buildSearchUrl(searchQuery, currentPage),
                    response -> {
                        List<OpenLibraryBook> newBooks = OpenLibraryBook.fromJson(response);
                        refreshBooksAdapter(newBooks, clear);

                        if (clear) {
                            hideShimmer();
                        }

                        if (newBooks.size() > 0) {
                            moreAvailable = true;
                        }
                    },
                    error -> {
                        hideShimmer();
                        Toast.makeText(requireContext(), R.string.ERROR_CONNECTION,
                                Toast.LENGTH_SHORT).show();
                    });

            UIHelper.hideKeyboard(requireActivity(), requireView());

            if (clear) {
                UIHelper.toggleViewIfOpposite(false, R.id.fragment_cover_search_book, requireActivity());
                UIHelper.toggleViewIfOpposite(false, R.id.fragment_cover_no_books_found, requireActivity());
                refreshBooksAdapter(null, true);
                showShimmer();
            }

            VolleyManager.getInstance(requireContext()).addToQueue(request);
        } else {
            Toast.makeText(requireContext(), R.string.ERROR_QUERY_EMPTY, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void bindTakePhotoButton() {
        FloatingActionButton addBookFab = binding.fragmentCoverCamera;
        addBookFab.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    ImageProcessor.takePictureFromCamera(this, requireActivity(), requireContext());
                } catch (IOException e) {
                    Toast.makeText(requireContext(), R.string.ERROR_PHOTO_PROCESSING, Toast.LENGTH_SHORT).show();
                }
            } else {
                ImageProcessor.requestCameraPermissionLauncher(this, requireActivity(), requireContext());
            }
        });
    }

    private void bindRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);

        RecyclerView recyclerView = binding.fragmentCoverRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int pastVisible = gridLayoutManager.findFirstVisibleItemPosition();
                    int visible = gridLayoutManager.getChildCount();
                    int total = gridLayoutManager.getItemCount();

                    if (moreAvailable && computeScrollingLoadRatio(pastVisible, visible, total)) {
                        moreAvailable = false;
                        currentPage++;
                        searchOpenLibrary(false);
                    }
                }
            }
        });

        booksAdapter = new OpenBookRecyclerViewAdapter(books, Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment));

        recyclerView.setAdapter(booksAdapter);
    }

    private void bindShimmer() {
        shimmerLayout = binding.fragmentCoverShimmer;
    }

    private void showShimmer() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.showShimmerAdapter();
    }

    private void hideShimmer() {
        shimmerLayout.hideShimmerAdapter();
    }

    private void refreshBooksAdapter(List<OpenLibraryBook> newBooks, boolean clear) {
        if (books != null && booksAdapter != null) {
            if (clear) {
                books.clear();
            }

            if (newBooks != null) {
                books.addAll(newBooks);

                if (books.isEmpty()) {
                    UIHelper.toggleView(true, R.id.fragment_cover_no_books_found, requireActivity());
                }
            }

            booksAdapter.notifyDataSetChanged();
        }
    }

    private boolean computeScrollingLoadRatio(int pastVisible, int visible, int total) {
        return (visible + pastVisible) >= 2 * (total / 3);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            String coverPath = "";

            if (resultCode == RESULT_OK) {
                coverPath = result.getUri().getPath();
            }

            NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);
            Objects.requireNonNull(nav.getPreviousBackStackEntry()).getSavedStateHandle().set("CUSTOM_COVER", coverPath);
            nav.popBackStack();
        }
    }
}
