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

public class QuotesFragment extends Fragment {

    private QuotesViewModel quotesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_quotes, container, false);

        final TextView textView = root.findViewById(R.id.text_dashboard);

        quotesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}
