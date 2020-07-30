package com.axbg.crimson.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books WHERE id = :id")
    BookEntity getById(long id);

    @Query("SELECT * FROM books ORDER BY id DESC")
    LiveData<List<BookEntity>> getAll();

    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' ORDER BY id DESC")
    List<BookEntity> getByTitle(String title);

    @Query("SELECT * FROM quotes WHERE book_id = :id")
    LiveData<List<QuoteEntity>> getQuotesByBookId(long id);

    @Query("SELECT COUNT(id) FROM books WHERE finished = :status")
    int countByFinishedStatus(boolean status);

    @Insert
    long create(BookEntity bookEntity);

    @Update
    int update(BookEntity bookEntity);

    @Query("DELETE FROM books WHERE id = :id")
    int delete(long id);

    @Query("DELETE FROM books")
    void deleteAll();
}
