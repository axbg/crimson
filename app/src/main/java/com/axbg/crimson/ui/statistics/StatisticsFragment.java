package com.axbg.crimson.ui.statistics;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;
import com.axbg.crimson.databinding.FragmentStatisticsBinding;
import com.axbg.crimson.ui.books.BooksViewModel;
import com.axbg.crimson.ui.quotes.QuotesViewModel;
import com.axbg.crimson.utility.UIHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StatisticsFragment extends Fragment {
    private QuotesViewModel quotesViewModel;
    private BooksViewModel booksViewModel;
    private FragmentStatisticsBinding binding;

    private float numberOfBooks = 0;
    private float numberOfQuotes = 0;

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
                    numberOfBooks = (float) books.size();
                    binding.fragmentStatisticsBooksNo.setText(String.valueOf((int) numberOfBooks));
                    calculateQuotesRate();
                });

        quotesViewModel.getLiveDataQuotes().observe(getViewLifecycleOwner(),
                (quotes) -> {
                    numberOfQuotes = (float) quotes.size();
                    binding.fragmentStatisticsQuotesNo.setText(String.valueOf((int) numberOfQuotes));
                    calculateQuotesRate();
                });
    }

    private void bindImport() {
        binding.fragmentStatisticsImport.setOnClickListener((v) -> {
            Toast.makeText(requireContext(), "Importing data", Toast.LENGTH_SHORT).show();
            // TODO
            // select file from storage
            // load data
        });
    }

    private void bindExport() {
        binding.fragmentStatisticsExport.setOnClickListener((v) -> {
            Toast.makeText(requireContext(), "Exporting data", Toast.LENGTH_SHORT).show();
            // TODO
            // create export file based on current date
            // serialize data
        });
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
                                            disableQuoteMenuItem();
                                        })
                                        .setNegativeButton(R.string.DIALOG_NO, null)
                                        .show())
                        .setNegativeButton(R.string.DIALOG_NO, null)
                        .show());
    }

    private void calculateQuotesRate() {
        if (numberOfBooks != 0 && numberOfQuotes != 0) {
            binding.fragmentStatisticsQuotesRate.setText(String.valueOf(numberOfQuotes / numberOfBooks));
        } else {
            binding.fragmentStatisticsQuotesRate.setText("0");
        }
    }

    private void disableQuoteMenuItem() {
        BottomNavigationView navView = requireActivity().findViewById(R.id.activity_landing_nav_view);

        if (navView != null) {
            navView.getMenu().getItem(0).setEnabled(false);
        }
    }
}