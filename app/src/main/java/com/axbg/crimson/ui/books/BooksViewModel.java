package com.axbg.crimson.ui.books;

import androidx.lifecycle.ViewModel;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class BooksViewModel extends ViewModel {

    private Map<Long, BookEntity> books;

    public BooksViewModel() {
        loadBooks();
    }

    private void loadBooks() {
        books = new HashMap<>();
        books.put((long) 1, new BookEntity("Title1", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 2, new BookEntity("Title2", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 3, new BookEntity("Title3", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 4, new BookEntity("Title4", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 5, new BookEntity("Title5", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 6, new BookEntity("Title6", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 7, new BookEntity("Title7", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 8, new BookEntity("Title8", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 9, new BookEntity("Title9", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 10, new BookEntity("Title10", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 11, new BookEntity("Title11", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.put((long) 12, new BookEntity("Title12", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
    }

    public BookEntity getBook(long bookId) {
        return books.get(bookId);
    }
}