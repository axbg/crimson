package com.axbg.crimson.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.room.Transaction;
import androidx.test.platform.app.InstrumentationRegistry;

import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QuoteDaoTest {
    private static final int NO_OF_QUOTES = 11;
    private static final int PAGE_SIZE = 10;

    private static DatabaseManager db;
    private static Context mockContext;

    @BeforeClass
    public static void setUpClass() {
        mockContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Before
    public void setUp() {
        db = Room.inMemoryDatabaseBuilder(mockContext, DatabaseManager.class).build();
        db.bookDao().create(new BookEntity("title", "author", LocalDate.now(), "cover"));

        QuoteEntity quote;
        for (int i = 0; i < NO_OF_QUOTES; i++) {
            quote = new QuoteEntity("quote_" + i, LocalDate.now(), 1);
            db.quoteDao().create(quote);
        }
    }

    @Test
    @Transaction()
    public void getById() {
        QuoteEntity result = db.quoteDao().getById(1);

        assertEquals(1, result.getId());
        assertEquals("quote_0", result.getText());
        assertEquals(LocalDate.now(), result.getAddedAt());
        assertEquals(1, result.getBookId());
    }

    @Test
    public void getByText() {
        List<QuoteEntity> result = db.quoteDao().getByText("quote");
        assertEquals(NO_OF_QUOTES, result.size());
    }

    @Test
    public void getPage() {
        List<QuoteEntity> firstPage = db.quoteDao().getPage(1, PAGE_SIZE);
        List<QuoteEntity> secondPage = db.quoteDao().getPage(2, PAGE_SIZE);

        assertEquals(PAGE_SIZE, firstPage.size());
        assertEquals(NO_OF_QUOTES - PAGE_SIZE, secondPage.size());
    }

    @Test
    public void getByBookId() {
        List<QuoteEntity> quotes = db.quoteDao().getByBookId(1);
        assertEquals(NO_OF_QUOTES, quotes.size());
    }

    @Test
    public void count() {
        int result = db.quoteDao().count();
        assertEquals(NO_OF_QUOTES, result);
    }

    @Test
    public void create() {
        QuoteEntity quoteEntity = new QuoteEntity("new quote", LocalDate.now(), 1);
        long id = db.quoteDao().create(quoteEntity);

        assertEquals(NO_OF_QUOTES + 1, id);
    }

    @Test
    public void update() {
        QuoteEntity quoteEntity = db.quoteDao().getById(1);
        quoteEntity.setText("something else");

        long result = db.quoteDao().update(quoteEntity);

        QuoteEntity updatedQuoteEntity = db.quoteDao().getById(1);

        assertEquals(1, result);
        assertEquals(quoteEntity.getId(), updatedQuoteEntity.getId());
        assertEquals(quoteEntity.getText(), updatedQuoteEntity.getText());
    }

    @Test
    public void delete() {
        QuoteEntity quoteEntity = db.quoteDao().getById(1);
        long result = db.quoteDao().delete(quoteEntity);

        QuoteEntity updatedQuoteEntity = db.quoteDao().getById(1);

        assertEquals(1, result);
        assertNull(updatedQuoteEntity);
    }

    @After
    public void tearDown() {
        db.close();
    }
}
