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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class QuotesFragment extends Fragment {
    private List<QuoteEntity> quotes;
    private QuotesAdapter quotesAdapter;
    private QuotesViewModel quotesViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_quotes, container, false);

        quotes = quotesViewModel.getQuotes();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindAddQuoteFab();
        bindListView();
    }

    private void bindAddQuoteFab() {
        FloatingActionButton addQuoteFab = requireView().findViewById(R.id.quotes_add_fab);
        addQuoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "element", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindListView() {
        quotesAdapter = new QuotesAdapter(quotes, R.layout.adapter_quotes, requireContext());
        ListView quotesListView = requireView().findViewById(R.id.quotes_listview);
        quotesListView.setAdapter(quotesAdapter);
    }
}
