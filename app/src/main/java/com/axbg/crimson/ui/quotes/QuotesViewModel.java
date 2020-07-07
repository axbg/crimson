package com.axbg.crimson.ui.quotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private QuoteDao quoteDao;
    private LiveData<List<QuoteEntity>> liveDataQuotes;

    private MutableLiveData<List<QuoteEntity>> liveDataQuotesWithBook = new MutableLiveData<>();

    @Setter
    private Function<Void, LiveData<Map<Long, BookEntity>>> getBookByIdLambda;

    public QuotesViewModel() {
        quoteDao = DatabaseManager.getInstance(null).quoteDao();
    }

    public void loadQuotes() throws Exception {
        liveDataQuotes = quoteDao.getAll();

        liveDataQuotes.observeForever(quotes -> {
            try {
                Map<Long, BookEntity> books = getBookByIdLambda.apply(null).getValue();
                if (books != null) {
                    mapBooksToQuotes(quotes, books);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        getBookByIdLambda.apply(null).observeForever(books -> {
            List<QuoteEntity> quotes = liveDataQuotes.getValue();
            if (quotes != null) {
                mapBooksToQuotes(quotes, books);
            }
        });
    }

    public void mapBooksToQuotes(List<QuoteEntity> quotes, Map<Long, BookEntity> books) {
        quotes.forEach(quote -> quote.setBook(books.get(quote.getBookId())));
        liveDataQuotesWithBook.setValue(quotes);
    }
}