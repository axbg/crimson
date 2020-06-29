package com.axbg.crimson.ui.books;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.axbg.crimson.dao.BookDao;
import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.db.entity.BookEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class BooksViewModel extends ViewModel {
    private BookDao bookDao;
    private LiveData<List<BookEntity>> liveDataBooks;
    private LiveData<Map<Long, BookEntity>> booksHashMap;

    public BooksViewModel() {
        bookDao = DatabaseManager.getInstance(null).bookDao();
        loadBooks();
    }

    private void loadBooks() {
        liveDataBooks = bookDao.getAll();
        booksHashMap = Transformations.map(liveDataBooks,
                books -> books.stream().collect(Collectors.toMap(BookEntity::getId, bookEntity -> bookEntity)));
    }

    public LiveData<Map<Long, BookEntity>> getBooksHashMap() {
        return booksHashMap;
    }
}