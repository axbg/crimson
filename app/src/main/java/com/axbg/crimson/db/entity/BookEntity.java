package com.axbg.crimson.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.axbg.crimson.db.Converters;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(tableName = "books")
public class BookEntity implements Serializable {
    private static final long serialVersionUID = -3854175523661032053L;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "added_at")
    private LocalDate addedAt;

    private boolean finished = false;

    @NonNull
    @ColumnInfo(name = "cover")
    private String coverPath;
}
