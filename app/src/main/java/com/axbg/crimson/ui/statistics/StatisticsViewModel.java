package com.axbg.crimson.ui.statistics;

import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class StatisticsViewModel extends ViewModel {
    private int quotesNumber = 0;
    private int booksNumber = 0;
    private int quotesRatio = 0;

    public StatisticsViewModel() {
        //populate with db data
    }
}