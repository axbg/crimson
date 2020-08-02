package com.axbg.crimson.ui.quotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentQuoteDetailBinding;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.axbg.crimson.utility.ImageProcessor;
import com.axbg.crimson.utility.UIHelper;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

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
        binding.fragmentQuoteDetailText.setOnFocusChangeListener(UIHelper.getOutOfFocusListener(requireActivity(), requireView()));

        binding.fragmentQuoteDetailCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    ImageProcessor.takePictureFromCamera(this, requireActivity(), requireContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ImageProcessor.requestCameraPermissionLauncher(this, requireActivity(), requireContext());
            }
        });

        binding.fragmentQuoteDetailGallery.setOnClickListener(v ->
                ImageProcessor.pickPictureFromGallery(this, requireActivity(), requireContext()));

        bindBookSelectListener();

        binding.fragmentQuoteDetailBook.setOnClickListener((v) ->
                NavHostFragment.findNavController(this).navigate(
                        QuoteDetailFragmentDirections.selectBookAction()
                ));

        binding.fragmentQuoteDetailAddButton.setOnClickListener(v -> {
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

                Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment)
                        .popBackStack();
            }
        });
    }

    private void bindViewLayout(long quoteId, String bookTitle) {
        binding.fragmentQuoteDetailAdded.setVisibility(View.VISIBLE);
        binding.fragmentQuoteDetailRemoveButton.setVisibility(View.VISIBLE);
        binding.fragmentQuoteDetailRemoveButton.setOnClickListener(v ->
                UIHelper.getAlertDialogBuilder(requireContext(), R.string.REMOVE_QUOTE, R.string.REMOVE_QUOTE_MESSAGE)
                        .setPositiveButton(R.string.DIALOG_YES, (dialog, which) -> {
                            AsyncTask.execute(() -> quotesViewModel.getQuoteDao().delete(quoteId));
                            Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment)
                                    .popBackStack();
                        })
                        .setNegativeButton(R.string.DIALOG_NO, null)
                        .show());

        binding.fragmentQuoteDetailBook.setText(bookTitle);

        AsyncTask.execute(() -> {
            existingQuote = quotesViewModel.getQuoteDao().getById(quoteId);

            if (existingQuote != null) {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                binding.fragmentQuoteDetailText.setText(existingQuote.getText());
                binding.fragmentQuoteDetailAdded.setText(existingQuote.getAddedAt().toString());
            }
        });
    }

    private QuoteEntity getInputValues() {
        binding.fragmentQuoteDetailText.setError(null);

        String text = binding.fragmentQuoteDetailText.getText().toString();
        if (text.isEmpty()) {
            binding.fragmentQuoteDetailText.setError(requireActivity().getText(R.string.ERROR_QUOTE_EMPTY));
            return null;
        }

        binding.fragmentQuoteDetailBook.setError(null);
        if (bookId == 0 && existingQuote == null) {
            binding.fragmentQuoteDetailBook.setError(requireActivity().getText(R.string.ERROR_BOOK_EMPTY));
            Toast.makeText(requireContext(), requireActivity().getText(R.string.ERROR_BOOK_EMPTY), Toast.LENGTH_SHORT).show();
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
        bookTitleListener.observe(getViewLifecycleOwner(), (bookTitle) -> binding.fragmentQuoteDetailBook.setText(bookTitle));
    }

    private void detectText() {
        try {
            InputImage quoteTempImage = InputImage.fromFilePath(requireContext(), quotePicture);
            TextRecognizer recognizer = TextRecognition.getClient();

            recognizer.process(quoteTempImage)
                    .addOnSuccessListener(text -> {
                        String extractedText = text.getText().replaceAll("\n", " ");
                        binding.fragmentQuoteDetailText.setText(extractedText);
                        binding.fragmentQuoteDetailText.setSelection(extractedText.length());
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(),
                            R.string.ERROR_NO_TEXT_DETECTED,
                            Toast.LENGTH_SHORT).show());
        } catch (IOException ignored) {
            Toast.makeText(requireContext(), R.string.ERROR_PHOTO_PROCESSING, Toast.LENGTH_SHORT)
                    .show();
        }
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
}