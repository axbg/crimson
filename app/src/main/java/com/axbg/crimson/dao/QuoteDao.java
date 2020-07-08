package com.axbg.crimson.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

@Dao
public interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE id = :id")
    QuoteEntity getById(long id);

    @Query("SELECT * FROM quotes")
    LiveData<List<QuoteEntity>> getAll();

    @Query("SELECT * FROM quotes WHERE text LIKE '%' || :text || '%'")
    List<QuoteEntity> getByText(String text);

    @Query("SELECT COUNT(id) FROM quotes")
    int count();

    @Insert
    long create(QuoteEntity quoteEntity);

    @Update
    int update(QuoteEntity quoteEntity);

    @Query("DELETE FROM quotes WHERE id = :id")
    int delete(long id);
}
