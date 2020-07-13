package com.axbg.crimson.ui.quotes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.axbg.crimson.BuildConfig;
import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentQuoteDetailBinding;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class QuoteDetailFragment extends Fragment {
    private FragmentQuoteDetailBinding binding;
    private QuotesViewModel quotesViewModel;

    private long bookId = 0;
    private Uri quotePicture;
    private QuoteEntity existingQuote;

    private ActivityResultLauncher<Uri> takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            saved -> {
                if (saved) {
                    cropPicture();
                }
            });

    private ActivityResultLauncher<String> galleryPick = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                quotePicture = uri;
                cropPicture();
            });

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePictureFromCamera();
                } else {
                    Toast.makeText(requireContext(), R.string.CAMERA_PERMISSION_MESSAGE, Toast.LENGTH_SHORT)
                            .show();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(requireActivity()).get(QuotesViewModel.class);

        binding = FragmentQuoteDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseArguments();
    }

    private void parseArguments() {
        boolean create = QuoteDetailFragmentArgs.fromBundle(requireArguments()).getCreate();
        long quoteId = QuoteDetailFragmentArgs.fromBundle(requireArguments()).getQuoteId();
        String bookTitle = QuoteDetailFragmentArgs.fromBundle(requireArguments()).getBookTitle();

        if (!create && quoteId != 0 && !bookTitle.equals("")) {
            bindViewLayout(quoteId, bookTitle);
        }

        bindLayout();
    }

    private void bindLayout() {
        binding.quoteDetailCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePictureFromCamera();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        binding.quoteDetailGallery.setOnClickListener(v -> galleryPick.launch("image/*"));

        bindBookSelectListener();

        binding.quoteDetailBookUpdate.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(
                        QuoteDetailFragmentDirections.selectBookAction()
                ));

        binding.quoteDetailAdd.setOnClickListener(v -> {
            QuoteEntity quote = getInputValues();

            if (quote != null) {
                AsyncTask.execute(() -> {
                    if (existingQuote == null) {
                        quotesViewModel.getQuoteDao().create(quote);
                    } else {
                        synchronizeQuotes(existingQuote, quote);
                        quotesViewModel.getQuoteDao().update(existingQuote);
                    }
                });

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .popBackStack();
            }
        });
    }

    private void bindViewLayout(long quoteId, String bookTitle) {
        binding.quoteDetailAdded.setVisibility(View.VISIBLE);
        binding.quoteDetailRemove.setVisibility(View.VISIBLE);
        binding.quoteDetailRemove.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.REMOVE_QUOTE)
                        .setMessage(R.string.REMOVE_QUOTE_MESSAGE)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.DIALOG_YES, (dialog, which) -> {
                            AsyncTask.execute(() -> quotesViewModel.getQuoteDao().delete(quoteId));
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                    .popBackStack();
                        })
                        .setNegativeButton(R.string.DIALOG_NO, null)
                        .show());

        binding.quoteDetailBook.setText(bookTitle);

        binding.quoteDetailText.setCursorVisible(false);
        binding.quoteDetailText.setOnClickListener((view) ->
                binding.quoteDetailText.setCursorVisible(true));

        AsyncTask.execute(() -> {
            existingQuote = quotesViewModel.getQuoteDao().getById(quoteId);

            if (existingQuote != null) {
                binding.quoteDetailText.setText(existingQuote.getText());
                binding.quoteDetailAdded.setText(existingQuote.getAddedAt().toString());
            }
        });
    }

    private QuoteEntity getInputValues() {
        binding.quoteDetailText.setError(null);

        String text = binding.quoteDetailText.getText().toString();
        if (text.isEmpty()) {
            binding.quoteDetailText.setError(String.valueOf(R.string.ERROR_QUOTE_EMPTY));
            return null;
        }

        binding.quoteDetailBook.setError(null);
        if (bookId == 0 && existingQuote == null) {
            binding.quoteDetailBook.setError(String.valueOf(R.string.ERROR_BOOK_EMPTY));
            return null;
        }

        return new QuoteEntity(text, LocalDate.now(), bookId);
    }

    private void synchronizeQuotes(QuoteEntity existingQuote, QuoteEntity quoteEntity) {
        existingQuote.setText(quoteEntity.getText());

        if (quoteEntity.getBookId() != 0) {
            existingQuote.setBookId(quoteEntity.getBookId());
        }

        quotesViewModel.getQuoteDao().update(existingQuote);
    }

    private void bindBookSelectListener() {
        NavController navController = NavHostFragment.findNavController(this);

        MutableLiveData<Long> bookIdListener = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData("BOOK_ID");
        bookIdListener.observe(getViewLifecycleOwner(), (newBookId) -> bookId = newBookId);

        MutableLiveData<String> bookTitleListener = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("BOOK_TITLE");
        bookTitleListener.observe(getViewLifecycleOwner(), (bookTitle) -> binding.quoteDetailBook.setText(bookTitle));
    }

    /* Image processing */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void takePictureFromCamera() {
        File tempPicture = new File(requireContext().getFilesDir(), "temp.png");

        try {
            if (!tempPicture.exists()) {
                tempPicture.createNewFile();
            }

            Uri uri = FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID.concat(".provider"),
                    tempPicture);

            quotePicture = uri;
            takePicture.launch(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cropPicture() {
        CropImage.activity(quotePicture)
                .start(requireContext(), this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                quotePicture = CropImage.getActivityResult(data).getUri();
                detectText();
            }
        }
    }

    private void detectText() {
        try {
            InputImage quoteTempImage = InputImage.fromFilePath(requireContext(), quotePicture);
            TextRecognizer recognizer = TextRecognition.getClient();

            recognizer.process(quoteTempImage)
                    .addOnSuccessListener(text -> {
                        binding.quoteDetailText.setText("");
                        String extractedText = text.getText();
                        binding.quoteDetailText.setText(extractedText);
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            R.string.ERROR_NO_TEXT_DETECTED,
                            Toast.LENGTH_SHORT).show());
        } catch (IOException ignored) {
            Toast.makeText(requireContext(), R.string.ERROR_PHOTO_PROCESSING, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}