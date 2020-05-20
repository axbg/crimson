package com.axbg.crimson.dao;

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
    BookEntity getById(int id);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :title || '%' ORDER BY id DESC")
    List<BookEntity> getByTitle(String title);

    @Query("SELECT * FROM books ORDER BY id DESC LIMIT 20 OFFSET (20 * :page)")
    List<BookEntity> getPage(int page);

    @Query("SELECT COUNT(id) FROM books WHERE finished = :status")
    int countByFinishedStatus(boolean status);

    @Insert
    long create(BookEntity bookEntity);

    @Update
    int update(BookEntity bookEntity);

    @Delete
    int delete(BookEntity bookEntity);
}
