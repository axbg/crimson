package com.axbg.crimson.db;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Converters {
    @TypeConverter
    public long fromLocalDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @TypeConverter
    public LocalDate fromTimestamp(long timestamp) {
        return LocalDate.from(new Date(timestamp).toInstant().atZone(ZoneId.systemDefault()));
    }
}
