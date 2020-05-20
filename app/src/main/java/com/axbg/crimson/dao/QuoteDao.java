package com.axbg.crimson.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

@Dao
public interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE id = :id")
    QuoteEntity getById(int id);

    @Query("SELECT * FROM quotes WHERE text LIKE '%' || :text || '%'")
    List<QuoteEntity> getByText(String text);

    @Query("SELECT * FROM quotes ORDER BY id DESC LIMIT 50 OFFSET (50 * (:page - 1))")
    List<QuoteEntity> getPage(int page);

    @Query("SELECT COUNT(id) FROM quotes")
    int count();

    @Insert
    long create(QuoteEntity quoteEntity);

    @Update
    int update(QuoteEntity quoteEntity);

    @Delete
    int delete(QuoteEntity quote);
}
