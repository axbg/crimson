package com.axbg.crimson.ui.quotes;

import androidx.lifecycle.ViewModel;

import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class QuotesViewModel extends ViewModel {
    private List<QuoteEntity> quotes;

    @Setter
    private Function<Long, BookEntity> bookByIdLambda;

    void loadBooks() throws InstantiationException {
        if (bookByIdLambda == null) {
            throw new InstantiationException("The booksByIdLambda was not populated");
        }

        quotes = new ArrayList<>();
        quotes.add(new QuoteEntity("Quote1", LocalDate.now(), 1));
        quotes.add(new QuoteEntity("Quote2", LocalDate.now(), 2));
        quotes.add(new QuoteEntity("Quote3", LocalDate.now(), 3));
        quotes.add(new QuoteEntity("Quote4", LocalDate.now(), 4));
        quotes.add(new QuoteEntity("Quote5", LocalDate.now(), 5));
        quotes.add(new QuoteEntity("Quote6", LocalDate.now(), 6));
        quotes.add(new QuoteEntity("Quote7", LocalDate.now(), 7));

        for (QuoteEntity quote : quotes) {
            quote.setBookEntity(bookByIdLambda.apply(quote.getBookId()));
        }
    }
}