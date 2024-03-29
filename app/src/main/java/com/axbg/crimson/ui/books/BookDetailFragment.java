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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentBookDetailBinding;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.axbg.crimson.utility.UIHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BookDetailFragment extends Fragment {
    private BookEntity existingBook;
    private BooksViewModel booksViewModel;
    private FragmentBookDetailBinding binding;
    private ArrayAdapter<QuoteEntity> quotesAdapter;

    private String imageUrl;
    private boolean createdCustomCover;

    private List<QuoteEntity> quotes = new ArrayList<>();

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

    private void bindLayout() {
        binding.fragmentBookDetailTitleText.setOnFocusChangeListener(UIHelper.getOutOfFocusListener(requireActivity(), requireView()));
        binding.fragmentBookDetailAuthorText.setOnFocusChangeListener(UIHelper.getOutOfFocusListener(requireActivity(), requireView()));
        binding.fragmentBookDetailAddButton.setOnClickListener(v -> {
            BookEntity book = getInputValues();
            if (book != null) {
                AsyncTask.execute(() -> {
                    try {
                        BitmapDrawable coverBitmap = (BitmapDrawable) binding.fragmentBookDetailCover.getDrawable();
                        downloadCover(book, coverBitmap.getBitmap());
                    } catch (IOException ignored) {
                    }

                    if (existingBook == null) {
                        booksViewModel.getBookDao().create(book);
                    } else {
                        synchronizeBooks(existingBook, book);
                        booksViewModel.getBookDao().update(existingBook);
                    }
                });

                Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment)
                        .popBackStack();
            }
        });

        binding.fragmentBookDetailCover.setOnClickListener(v -> {
            NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);
            nav.navigate(BookDetailFragmentDirections.addCoverAction());
        });

        bindCoverFragmentUrlListener();
    }

    private void buildViewLayout(long bookId) {
        binding.fragmentBookDetailRemoveButton.setVisibility(View.VISIBLE);
        binding.fragmentBookDetailRemoveButton.setOnClickListener(v ->
                UIHelper.getAlertDialogBuilder(requireContext(), R.string.REMOVE_BOOK, R.string.REMOVE_BOOK_MESSAGE)
                        .setPositiveButton(R.string.DIALOG_YES, (dialog, which) -> {
                            AsyncTask.execute(() -> booksViewModel.getBookDao().delete(bookId));
                            Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment)
                                    .popBackStack();
                        })
                        .setNegativeButton(R.string.DIALOG_NO, null)
                        .show());

        BookEntity bookEntity = Objects.requireNonNull(booksViewModel.getBooksHashMap().getValue()).get(bookId);
        existingBook = bookEntity;

        if (bookEntity != null) {
            binding.fragmentBookDetailCover.setImageBitmap(loadCover(bookEntity.getCoverPath()));
            binding.fragmentBookDetailTitleText.setText(bookEntity.getTitle());
            binding.fragmentBookDetailAuthorText.setText(bookEntity.getAuthor());
            binding.fragmentBookDetailFinished.setChecked(bookEntity.isFinished());

            bindQuotesAdapter(quotes, bookEntity.getTitle());

            booksViewModel.getBookDao().getQuotesByBookId(bookId).observe(getViewLifecycleOwner(),
                    (quotes) -> {
                        if (!quotes.isEmpty()) {
                            refreshQuotesAdapter(quotes);
                            binding.fragmentBookDetailQuotes.setVisibility(View.VISIBLE);
                        } else {
                            binding.fragmentBookDetailQuotes.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void bindQuotesAdapter(List<QuoteEntity> quotes, String bookTitle) {
        quotesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, quotes);

        binding.fragmentBookDetailQuotes.setAdapter(quotesAdapter);
        binding.fragmentBookDetailQuotes.setOnItemClickListener(((parent, view, position, id) -> {
            BookDetailFragmentDirections.ViewBookQuoteAction action = BookDetailFragmentDirections.viewBookQuoteAction();

            action.setCreate(false);
            action.setQuoteId(quotes.get(position).getId());
            action.setBookTitle(bookTitle);

            Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment).navigate(action);
        }));
    }

    private void refreshQuotesAdapter(List<QuoteEntity> newQuotes) {
        if (quotes != null && quotesAdapter != null) {
            quotes.clear();

            if (newQuotes != null) {
                quotes.addAll(newQuotes);
            }

            quotesAdapter.notifyDataSetChanged();
        }
    }

    private void bindCoverFragmentUrlListener() {
        NavController navController = NavHostFragment.findNavController(this);

        MutableLiveData<OpenLibraryBook> openBookData = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData("OPEN_BOOK");
        openBookData.observe(getViewLifecycleOwner(), (book) -> {
            binding.fragmentBookDetailTitleText.setText(book.getTitle());
            binding.fragmentBookDetailAuthorText.setText(book.getAuthor());
            imageUrl = book.getEditionKey();
            Picasso.get().load(NetworkUtil.buildCoverUrl(imageUrl)).into(binding.fragmentBookDetailCover);
        });

        MutableLiveData<String> customCover = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData("CUSTOM_COVER");
        customCover.observe(getViewLifecycleOwner(), (coverPath) -> {
            createdCustomCover = true;
            binding.fragmentBookDetailCover.setImageBitmap(loadCover(coverPath));
            removeFromCache(coverPath);
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void removeFromCache(String cacheLocation) {
        File file = new File(cacheLocation);
        if (file.exists()) {
            file.delete();
        }
    }

    private Bitmap loadCover(String location) {
        File file = new File(location);
        Bitmap bitmap;

        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap.getHeight() > 1 && bitmap.getWidth() > 1) {
                return bitmap;
            }
        }

        return BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.cover_add_sample);
    }

    private BookEntity getInputValues() {
        binding.fragmentBookDetailTitleText.setError(null);
        binding.fragmentBookDetailAuthorText.setError(null);

        if (((imageUrl == null || imageUrl.isEmpty()) && existingBook == null) && !createdCustomCover) {
            Toast.makeText(requireContext(), requireActivity().getString(R.string.ERROR_COVER_IMAGE_EMPTY), Toast.LENGTH_SHORT).show();
            return null;
        }

        String title = Objects.requireNonNull(binding.fragmentBookDetailTitleText.getText()).toString();
        if (title.isEmpty()) {
            binding.fragmentBookDetailTitleText.setError(requireActivity().getString(R.string.ERROR_TITLE_EMPTY));
            return null;
        }

        String author = Objects.requireNonNull(binding.fragmentBookDetailAuthorText.getText()).toString();
        if (author.isEmpty()) {
            binding.fragmentBookDetailAuthorText.setError(requireActivity().getString(R.string.ERROR_AUTHOR_EMPTY));
            return null;
        }

        BookEntity bookEntity = new BookEntity(title, author, LocalDate.now(), "");
        bookEntity.setFinished(binding.fragmentBookDetailFinished.isChecked());
        return bookEntity;
    }

    private void downloadCover(BookEntity book, Bitmap cover) throws IOException {
        String path = UUID.randomUUID().toString() + ".png";
        Bitmap resizedBitmap = resizeBitmap(cover);
        File coverFile = new File(requireContext().getFilesDir(), path);

        if ((imageUrl != null || createdCustomCover) && resizedBitmap != null) {
            if (coverFile.createNewFile()) {
                try (OutputStream outputStream = new FileOutputStream(coverFile)) {
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    book.setCoverPath(coverFile.getAbsolutePath());
                }
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int ratio = 1;
            int bitmapHeight = bitmap.getHeight();

            while (bitmapHeight > 450) {
                ratio++;
                bitmapHeight = (bitmap.getHeight() / ratio);
            }

            return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / ratio, bitmapHeight, true);
        }

        return null;
    }

    private void synchronizeBooks(BookEntity existingBook, BookEntity newBook) {
        existingBook.setTitle(newBook.getTitle());
        existingBook.setAuthor(newBook.getAuthor());
        existingBook.setFinished(newBook.isFinished());

        if (!newBook.getCoverPath().isEmpty()) {
            existingBook.setCoverPath(newBook.getCoverPath());
        }
    }
}