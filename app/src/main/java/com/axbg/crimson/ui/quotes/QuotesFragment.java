package com.axbg.crimson.ui.quotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import lombok.SneakyThrows;

public class QuotesFragment extends Fragment {
    private QuotesAdapter quotesAdapter;
    private QuotesViewModel quotesViewModel;
    private ShimmerRecyclerView shimmerLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(requireActivity()).get(QuotesViewModel.class);
        return inflater.inflate(R.layout.fragment_quotes, container, false);
    }

    @SneakyThrows
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindAddQuoteFab();
        bindShimmer();

        quotesViewModel.getLiveDataQuotes().observe(getViewLifecycleOwner(), (quoteEntities -> {
            shimmerLayout.hideShimmerAdapter();
            bindListView(quoteEntities);
        }));
    }

    private void bindAddQuoteFab() {
        FloatingActionButton addQuoteFab = requireView().findViewById(R.id.quotes_add_fab);
        addQuoteFab.setOnClickListener(v -> Toast.makeText(getContext(), "Add quote", Toast.LENGTH_SHORT).show());
    }

    private void bindListView(List<QuoteEntity> quotes) {
        try {
            quotesAdapter = new QuotesAdapter(quotes, R.layout.adapter_quotes, requireContext());
            ListView quotesListView = requireView().findViewById(R.id.quotes_listview);
            quotesListView.setAdapter(quotesAdapter);
        } catch (Exception ignored) {
        }
    }

    private void bindShimmer() {
        shimmerLayout = requireView().findViewById(R.id.fragment_quotes_shimmer);
        shimmerLayout.showShimmerAdapter();
    }
}
