package com.axbg.crimson.ui.quotes;

import androidx.lifecycle.ViewModel;

import com.axbg.crimson.db.entity.QuoteEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class QuotesViewModel extends ViewModel {
    private List<QuoteEntity> quotes;

    public QuotesViewModel() {
        // load from db
        quotes = new ArrayList<>();
        quotes.add(new QuoteEntity("Quote1", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote2", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote3", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote4", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote5", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote6", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote7", LocalDate.now(), 1));
    }

    List<QuoteEntity> getQuotes() {
        return this.quotes;
    }
}