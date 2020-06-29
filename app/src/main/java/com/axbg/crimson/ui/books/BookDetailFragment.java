package com.axbg.crimson.ui.books;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.axbg.crimson.databinding.FragmentBookDetailBinding;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class BookDetailFragment extends Fragment {
    private FragmentBookDetailBinding binding;
    private BooksViewModel booksViewModel;
    private String imageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        binding = FragmentBookDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseArguments();
    }

    private void parseArguments() {
        boolean create = BookDetailFragmentArgs.fromBundle(requireArguments()).getCreate();
        long bookId = BookDetailFragmentArgs.fromBundle(requireArguments()).getBookId();

        if (!create && bookId != 0) {
            buildViewLayout(bookId);
        }

        bindLayout();
    }

    private void buildViewLayout(long bookId) {
        binding.bookDetailQuotes.setVisibility(View.VISIBLE);
        binding.bookDetailRemove.setVisibility(View.VISIBLE);
        binding.bookDetailRemove.setOnClickListener(v -> AsyncTask.execute(() -> {
            booksViewModel.getBookDao().delete(bookId);
            getParentFragmentManager().popBackStack();
        }));

        BookEntity bookEntity = Objects.requireNonNull(booksViewModel.getBooksHashMap().getValue()).get(bookId);

        assert bookEntity != null;
        binding.bookDetailCover.setImageBitmap(loadCover(bookEntity.getCoverPath()));
        binding.bookDetailTitleText.setText(bookEntity.getTitle());
        binding.bookDetailAuthorText.setText(bookEntity.getAuthor());
        binding.bookDetailFinished.setChecked(bookEntity.isFinished());
        booksViewModel.getBookDao().getQuotesByBookId(bookId).observe(getViewLifecycleOwner(), quoteEntities -> {
            List<String> actualQuotes = quoteEntities.stream()
                    .map(QuoteEntity::getShortText)
                    .collect(Collectors.toList());

            ArrayAdapter<String> quotesAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, actualQuotes);

            binding.bookDetailQuotes.setAdapter(quotesAdapter);
        });
    }

    private void bindLayout() {
        binding.bookDetailAdd.setOnClickListener(v -> {
            BookEntity book = getInputValues();
            if (book != null) {
                AsyncTask.execute(() -> {
                    try {
                        BitmapDrawable coverBitmap = (BitmapDrawable) binding.bookDetailCover.getDrawable();
                        book.setCoverPath(downloadCover(coverBitmap.getBitmap()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    booksViewModel.getBookDao().create(book);
                    getParentFragmentManager().popBackStack();
                });
            }
        });

        bindCoverFragmentUrlListener();
        binding.bookDetailCover.setOnClickListener(v -> {
            // open CoverFragment
        });
    }

    private void bindCoverFragmentUrlListener() {
        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<String> coverUrl = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData("COVER_URL");
        coverUrl.observe(getViewLifecycleOwner(), (url) -> {
            imageUrl = url;
            Picasso.get().load(url).into(binding.bookDetailCover);
        });
    }

    private Bitmap loadCover(String location) {
        File file = new File(location);
        return file.exists() ? BitmapFactory.decodeFile(file.getAbsolutePath())
                : null;
    }

    private BookEntity getInputValues() {
        binding.bookDetailTitleText.setError(null);
        binding.bookDetailAuthorText.setError(null);

        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Cover image cannot be empty", Toast.LENGTH_SHORT).show();
            return null;
        }

        String title = Objects.requireNonNull(binding.bookDetailTitleText.getText()).toString();
        if (title.isEmpty()) {
            binding.bookDetailTitleText.setError("Title cannot be empty");
            return null;
        }

        String author = Objects.requireNonNull(binding.bookDetailAuthorText.getText()).toString();
        if (author.isEmpty()) {
            binding.bookDetailTitleText.setError("Author cannot be empty");
            return null;
        }

        BookEntity bookEntity = new BookEntity(title, author, LocalDate.now(), null);
        bookEntity.setFinished(binding.bookDetailFinished.isChecked());
        return bookEntity;
    }

    private String downloadCover(Bitmap cover) throws IOException {
        String path = UUID.randomUUID().toString() + ".png";
        File coverFile = new File(requireContext().getFilesDir(), path);
        boolean result = coverFile.createNewFile();

        if (result) {
            try (OutputStream outputStream = new FileOutputStream(path)) {
                cover.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
            }
        }

        return result ? coverFile.getAbsolutePath() : "";
    }
}