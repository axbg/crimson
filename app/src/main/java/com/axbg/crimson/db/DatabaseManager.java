package com.axbg.crimson.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.axbg.crimson.dao.BookDao;
import com.axbg.crimson.dao.QuoteDao;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

@Database(entities = {QuoteEntity.class, BookEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {
    private static volatile DatabaseManager instance;

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null && context != null) {
                    instance = Room.databaseBuilder(context, DatabaseManager.class, "crimson1-db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract QuoteDao quoteDao();

    public abstract BookDao bookDao();
}
