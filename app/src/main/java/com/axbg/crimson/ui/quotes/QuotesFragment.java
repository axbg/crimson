package com.axbg.crimson.ui.quotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QuotesFragment extends Fragment {
    private QuotesAdapter quotesAdapter;
    private QuotesViewModel quotesViewModel;
    private ShimmerRecyclerView shimmerLayout;

    private List<QuoteEntity> quotes = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(requireActivity()).get(QuotesViewModel.class);
        return inflater.inflate(R.layout.fragment_quotes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindAddQuoteFab();
        bindShimmer();
        bindListView(quotes);

        quotesViewModel.getLiveDataQuotesWithBook().observe(getViewLifecycleOwner(), quoteEntities -> {
            shimmerLayout.hideShimmerAdapter();
            refreshListView(quoteEntities);
        });
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_quotes_shimmer);
        shimmerLayout.showShimmerAdapter();
    }

    private void bindListView(List<QuoteEntity> quotes) {
        try {
            quotesAdapter = new QuotesAdapter(quotes, R.layout.adapter_quotes, requireContext());

            ListView quotesListView = requireView().findViewById(R.id.fragment_quotes_list_view);
            quotesListView.setAdapter(quotesAdapter);
            quotesListView.setOnItemClickListener(((parent, view, position, id) -> {
                String bookTitle = quotes.get(position).getBook() != null ?
                        quotes.get(position).getBook().getTitle() : "";

                launchCreateQuoteAction(false, quotes.get(position).getId(), bookTitle);
            }));
        } catch (Exception ignored) {
        }
    }

    private void bindAddQuoteFab() {
        FloatingActionButton addQuoteFab = requireView().findViewById(R.id.fragment_quotes_add);
        addQuoteFab.setOnClickListener(v ->
                launchCreateQuoteAction(true, 0, ""));
    }

    private void refreshListView(List<QuoteEntity> newQuotes) {
        if (quotes != null && quotesAdapter != null) {
            quotes.clear();

            if (newQuotes != null) {
                quotes.addAll(newQuotes);
            }

            quotesAdapter.notifyDataSetChanged();
        }
    }

    private void launchCreateQuoteAction(boolean create, long quoteId, String bookTitle) {
        NavController nav = Navigation.findNavController(requireActivity(), R.id.activity_landing_nav_host_fragment);

        QuotesFragmentDirections.CreateQuoteAction action = QuotesFragmentDirections.createQuoteAction();
        action.setCreate(create);
        action.setQuoteId(quoteId);
        action.setBookTitle(bookTitle);

        nav.navigate(action);
    }
}
