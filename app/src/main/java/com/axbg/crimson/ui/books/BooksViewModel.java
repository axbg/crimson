package com.axbg.crimson.ui.books;

import androidx.lifecycle.ViewModel;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BooksViewModel extends ViewModel {

    private List<BookEntity> books;

    public BooksViewModel() {
        books = new ArrayList<>();
        books.add(new BookEntity("Title1", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title2", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title3", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title4", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title1", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title2", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title3", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title4", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title1", "Author1", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title2", "Author2", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title3", "Author3", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
        books.add(new BookEntity("Title4", "Author4", LocalDate.now(), String.valueOf(R.drawable.cover_sample)));
    }

    public List<BookEntity> getBooks() {
        return this.books;
    }
}