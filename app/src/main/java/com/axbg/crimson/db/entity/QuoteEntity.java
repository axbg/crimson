package com.axbg.crimson.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.axbg.crimson.db.Converters;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static androidx.room.ForeignKey.CASCADE;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(tableName = "quotes",
        foreignKeys = @ForeignKey(entity = BookEntity.class,
                parentColumns = "id",
                childColumns = "book_id",
                onDelete = CASCADE),
        indices = {@Index("book_id")})
public class QuoteEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String text;

    @NonNull
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "added_at")
    private LocalDate addedAt;

    @NonNull
    @ColumnInfo(name = "book_id")
    private long bookId;

    @Ignore
    private BookEntity book;

    public String getShortText() {
        return this.text.length() > 300 ? this.text.substring(0, 300) + "..." : this.text;
    }
}
