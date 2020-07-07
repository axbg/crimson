package com.axbg.crimson.ui.quotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.databinding.FragmentQuoteDetailBinding;
import com.axbg.crimson.ui.books.BooksViewModel;

import org.jetbrains.annotations.NotNull;

public class QuoteDetailFragment extends Fragment {

    FragmentQuoteDetailBinding binding;
    QuotesViewModel quotesViewModel;
    BooksViewModel booksViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(requireActivity()).get(QuotesViewModel.class);
        booksViewModel = new ViewModelProvider(requireActivity()).get(BooksViewModel.class);

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

    }

    private void bindViewLayout(long quoteId, String bookTitle) {

    }
}