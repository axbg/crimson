package com.axbg.crimson.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axbg.crimson.db.entity.BookEntity;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books WHERE id = :id")
    BookEntity getById(long id);

    @Query("SELECT * FROM books")
    LiveData<List<BookEntity>> getAll();

    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' ORDER BY id DESC")
    List<BookEntity> getByTitle(String title);

    @Query("SELECT COUNT(id) FROM books WHERE finished = :status")
    int countByFinishedStatus(boolean status);

    @Insert
    long create(BookEntity bookEntity);

    @Update
    int update(BookEntity bookEntity);

    @Delete
    int delete(BookEntity bookEntity);
}
