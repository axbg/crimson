package com.axbg.crimson.ui.quotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuotesFragment extends Fragment {

    private QuotesViewModel quotesViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_quotes, container, false);

        final TextView textView = root.findViewById(R.id.text_quotes);
        quotesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindAddQuoteFab();
    }

    private void bindAddQuoteFab() {
        FloatingActionButton addQuoteFab = requireView().findViewById(R.id.add_quote_fab);
        addQuoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open BookEditActivity
            }
        });
    }
}
