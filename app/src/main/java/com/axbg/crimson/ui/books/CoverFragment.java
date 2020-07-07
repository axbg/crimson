package com.axbg.crimson.ui.books;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.axbg.crimson.BuildConfig;
import com.axbg.crimson.R;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.VolleyManager;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.axbg.crimson.ui.books.adapter.OpenLibraryBooksAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class CoverFragment extends Fragment {
    private Uri cameraPicture;
    private ShimmerRecyclerView shimmerLayout;
    private OpenLibraryBooksAdapter booksAdapter;

    private List<OpenLibraryBook> books = new ArrayList<>();

    private ActivityResultLauncher<Uri> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            saved -> {
                if (saved) {
                    cropPicture();
                }
            });

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePictureFromCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT)
                            .show();
                }
            });

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
            String searchQuery = searchQueryInput.getText() != null ?
                    searchQueryInput.getText().toString() : "";

            if (!searchQuery.isEmpty()) {
                StringRequest request = new StringRequest(Request.Method.GET, NetworkUtil.buildSearchUrl(searchQuery),
                        response -> {
                            refreshBooksAdapter(OpenLibraryBook.fromJson(response));
                            hideShimmer();
                        },
                        error -> {
                            hideShimmer();
                            Toast.makeText(requireContext(), "Error during connection",
                                    Toast.LENGTH_SHORT).show();
                        });

                hideKeyboard();
                showShimmer();
                refreshBooksAdapter(null);
                VolleyManager.getInstance(requireContext()).addToQueue(request);
            } else {
                Toast.makeText(requireContext(), "Query text cannot be empty", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void bindTakePhotoButton() {
        FloatingActionButton addBookFab = requireView().findViewById(R.id.cover_camera_fab);
        addBookFab.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureFromCamera();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void bindGridView(List<OpenLibraryBook> books) {
        try {
            booksAdapter = new OpenLibraryBooksAdapter(books, R.layout.adapter_books, requireContext());

            GridView booksGridView = requireView().findViewById(R.id.cover_grid_view);
            booksGridView.setAdapter(booksAdapter);
            booksGridView.setOnItemClickListener((parent, view, position, id) -> {
                NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Objects.requireNonNull(nav.getPreviousBackStackEntry()).
                        getSavedStateHandle().set("OPEN_BOOK", books.get(position));
                nav.popBackStack();
            });
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

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(requireView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void takePictureFromCamera() {
        File tempPicture = new File(requireContext().getFilesDir(), "temp.png");

        try {
            if (!tempPicture.exists()) {
                tempPicture.createNewFile();
            }

            Uri uri = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID.concat(".provider"),
                    tempPicture);

            cameraPicture = uri;
            takePicture.launch(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cropPicture() {
        CropImage.activity(cameraPicture)
                .start(requireContext(), this);
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

            NavController nav = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            Objects.requireNonNull(nav.getPreviousBackStackEntry()).
                    getSavedStateHandle().set("CUSTOM_COVER", coverPath);
            nav.popBackStack();
        }
    }
}