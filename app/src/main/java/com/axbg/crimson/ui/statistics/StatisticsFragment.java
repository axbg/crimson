package com.axbg.crimson.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axbg.crimson.R;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindProfilePictureClick();
        bindStatistics();
    }

    private void bindProfilePictureClick() {
        ImageView profilePicture = requireView().findViewById(R.id.statistics_profile_picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "You're awesome!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindStatistics() {
        TextView quotesNo = requireView().findViewById(R.id.statistics_quotes_no);
        quotesNo.setText(String.valueOf(statisticsViewModel.getQuotesNumber()));

        TextView booksNo = requireView().findViewById(R.id.statistics_books_no);
        booksNo.setText(String.valueOf(statisticsViewModel.getBooksNumber()));

        TextView quotesRatio = requireView().findViewById(R.id.statistics_quotes_rate);
        quotesRatio.setText(String.valueOf(statisticsViewModel.getQuotesRatio()));
    }
}