package com.axbg.crimson.ui.quotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.axbg.crimson.dao.QuoteDao;
import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;
import lombok.Getter;
import lombok.Setter;

@Getter
public class QuotesViewModel extends ViewModel {
    @Setter
    private Function<Void, LiveData<Map<Long, BookEntity>>> getBookByIdLambda;
    private LiveData<List<QuoteEntity>> liveDataQuotes;
    private QuoteDao quoteDao;

    public QuotesViewModel() {
        quoteDao = DatabaseManager.getInstance(null).quoteDao();
    }

    public void loadQuotes() throws Exception {
        liveDataQuotes = quoteDao.getAll();
        getBookByIdLambda.apply(null).observeForever(books -> {
            if (liveDataQuotes.getValue() != null) {
                liveDataQuotes.getValue()
                        .forEach(quote -> quote.setBook(books.get(quote.getBookId())));
            }
        });
    }
}