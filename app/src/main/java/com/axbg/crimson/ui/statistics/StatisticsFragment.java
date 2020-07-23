package com.axbg.crimson.ui.statistics;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentStatisticsBinding;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.axbg.crimson.ui.books.BooksViewModel;
import com.axbg.crimson.ui.quotes.QuotesViewModel;
import com.axbg.crimson.utility.UIHelper;
import com.axbg.crimson.utility.serializable.ByteArrayWrapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatisticsFragment extends Fragment {
    private QuotesViewModel quotesViewModel;
    private BooksViewModel booksViewModel;
    private FragmentStatisticsBinding binding;

    private float numberOfBooks = 0;
    private float numberOfQuotes = 0;

    private List<BookEntity> books = new ArrayList<>();
    private List<QuoteEntity> quotes = new ArrayList<>();

    private ActivityResultLauncher<String> createDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument(),
            uri -> {
                if (uri != null) {
                    exportFile(uri);
                }
            });

    private ActivityResultLauncher<String> getDocument = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    importFile(uri);
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);
        quotesViewModel = new ViewModelProvider(requireActivity()).get(QuotesViewModel.class);

        binding = FragmentStatisticsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindLayout();
    }

    private void bindLayout() {
        bindProfilePictureClick();
        bindStatistics();
        bindImport();
        bindExport();
        bindReset();
    }

    private void bindProfilePictureClick() {
        binding.fragmentStatisticsProfilePicture.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.YOU_AWESOME_EASTER_EGG, Toast.LENGTH_SHORT).show());
    }

    private void bindStatistics() {
        booksViewModel.getLiveDataBooks().observe(getViewLifecycleOwner(),
                (books) -> {
                    this.books = books;
                    numberOfBooks = (float) books.size();
                    binding.fragmentStatisticsBooksNo.setText(String.valueOf((int) numberOfBooks));
                    calculateQuotesRate();
                });

        quotesViewModel.getLiveDataQuotes().observe(getViewLifecycleOwner(),
                (quotes) -> {
                    this.quotes = quotes;
                    numberOfQuotes = (float) quotes.size();
                    binding.fragmentStatisticsQuotesNo.setText(String.valueOf((int) numberOfQuotes));
                    calculateQuotesRate();

                    if (quotes.size() != 0) {
                        toggleQuoteMenuItem(true);
                    }
                });
    }

    private void bindImport() {
        binding.fragmentStatisticsImport.setOnClickListener((v) -> getDocument.launch("*/*"));
    }

    private void bindExport() {
        binding.fragmentStatisticsExport.setOnClickListener((v) ->
                createDocument.launch("crimson_export_" + LocalDate.now().toString() + ".bin"));
    }

    private void bindReset() {
        binding.fragmentStatisticsReset.setOnClickListener((v) ->
                UIHelper.getAlertDialogBuilder(requireContext(), R.string.RESET_LIBRARY, R.string.RESET_LIBRARY_MESSAGE)
                        .setPositiveButton(R.string.DIALOG_YES, (dialog, which) ->
                                UIHelper.getAlertDialogBuilder(requireContext(), R.string.RESET_LIBRARY, R.string.RESET_LIBRARY_CONFIRMATION)
                                        .setPositiveButton(R.string.DIALOG_YES, (secondDialog, secondWhich) -> {
                                            AsyncTask.execute(() -> {
                                                quotesViewModel.getQuoteDao().deleteAll();
                                                booksViewModel.getBookDao().deleteAll();
                                            });
                                            toggleQuoteMenuItem(false);
                                        })
                                        .setNegativeButton(R.string.DIALOG_NO, null)
                                        .show())
                        .setNegativeButton(R.string.DIALOG_NO, null)
                        .show());
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void exportFile(Uri uri) {
        AsyncTask<Void, String, Void> exportFileAsyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    OutputStream outputStream = requireActivity().getContentResolver().openOutputStream(uri);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

                    objectOutputStream.writeInt(books.size());
                    for (BookEntity bookEntity : books) {
                        objectOutputStream.writeObject(bookEntity);
                        File coverFile = new File(bookEntity.getCoverPath());

                        if (coverFile.exists()) {
                            byte[] coverBytes = new byte[(int) coverFile.length()];

                            FileInputStream coverFileInputStream = new FileInputStream(coverFile);
                            coverFileInputStream.read(coverBytes);
                            coverFileInputStream.close();

                            objectOutputStream.writeObject(new ByteArrayWrapper(coverBytes));
                        }
                    }

                    objectOutputStream.writeInt(quotes.size());
                    for (QuoteEntity quoteEntity : quotes) {
                        objectOutputStream.writeObject(quoteEntity);
                    }

                    objectOutputStream.close();
                    Objects.requireNonNull(outputStream).close();

                    publishProgress(requireContext().getString(R.string.EXPORT_LIBRARY_SUCCESS));
                } catch (IOException e) {
                    publishProgress(requireContext().getString(R.string.ERROR_EXPORT_FILE));
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                Toast.makeText(requireContext(), values[0], Toast.LENGTH_SHORT).show();
            }
        };

        exportFileAsyncTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void importFile(Uri uri) {
        AsyncTask<Void, String, Void> importFileAsyncTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    publishProgress("Import process started");

                    InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                    int numberOfBooks = objectInputStream.readInt();
                    List<BookEntity> importedBooks = new ArrayList<>();
                    for (int i = 0; i < numberOfBooks; i++) {
                        BookEntity bookEntity = (BookEntity) objectInputStream.readObject();
                        importedBooks.add(bookEntity);

                        ByteArrayWrapper cover = ((ByteArrayWrapper) objectInputStream.readObject());
                        saveFile(cover.getContent(), bookEntity.getCoverPath());
                    }

                    int numberOfQuotes = objectInputStream.readInt();
                    List<QuoteEntity> importedQuotes = new ArrayList<>();
                    for (int i = 0; i < numberOfQuotes; i++) {
                        importedQuotes.add((QuoteEntity) objectInputStream.readObject());
                    }

                    long bookIdOffset = books.size() > 0 ? books.get(books.size() - 1).getId() : 0;
                    long quoteIdOffset = quotes.size() > 0 ? quotes.get(quotes.size() - 1).getId() : 0;

                    importToDatabase(bookIdOffset, importedBooks, quoteIdOffset, importedQuotes);

                    publishProgress(requireContext().getString(R.string.IMPORT_LIBRARY_SUCCESS));
                } catch (IOException | ClassNotFoundException e) {
                    publishProgress(requireContext().getString(R.string.ERROR_IMPORT_FILE));
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                Toast.makeText(requireContext(), values[0], Toast.LENGTH_SHORT).show();
            }
        };

        importFileAsyncTask.execute();
    }

    private void calculateQuotesRate() {
        if (numberOfBooks != 0 && numberOfQuotes != 0) {
            binding.fragmentStatisticsQuotesRate.setText(String.valueOf(numberOfQuotes / numberOfBooks));
        } else {
            binding.fragmentStatisticsQuotesRate.setText("0");
        }
    }

    private void toggleQuoteMenuItem(boolean state) {
        BottomNavigationView navView = requireActivity().findViewById(R.id.activity_landing_nav_view);

        if (navView != null) {
            navView.getMenu().getItem(0).setEnabled(state);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveFile(byte[] cover, String coverPath) {
        try {
            File coverFile = new File(coverPath);

            if (coverFile.exists()) {
                coverFile.delete();
            }

            coverFile.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(coverFile);
            fileOutputStream.write(cover);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importToDatabase(long bookIdOffset, List<BookEntity> importedBooks,
                                  long quoteIdOffset, List<QuoteEntity> importedQuotes) {
        Map<Long, Long> oldBookIds = new HashMap<>();

        for (int i = 0; i < importedBooks.size(); i++) {
            BookEntity bookEntity = importedBooks.get(i);

            if (bookIdOffset != 0) {
                long oldId = bookEntity.getId();
                bookEntity.setId(bookIdOffset + i + 1);
                oldBookIds.put(oldId, bookEntity.getId());
            }

            booksViewModel.getBookDao().create(bookEntity);
        }

        for (int i = 0; i < importedQuotes.size(); i++) {
            QuoteEntity quoteEntity = importedQuotes.get(i);

            if (quoteIdOffset != 0) {
                quoteEntity.setId(quoteIdOffset + i + 1);
            }

            if (bookIdOffset != 0) {
                //noinspection ConstantConditions
                quoteEntity.setBookId(oldBookIds.get(quoteEntity.getBookId()));
            }

            quotesViewModel.getQuoteDao().create(quoteEntity);
        }
    }
}