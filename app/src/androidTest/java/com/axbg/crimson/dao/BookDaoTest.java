package com.axbg.crimson.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.db.entity.BookEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BookDaoTest {
    private static final int NO_OF_QUOTES = 12;
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

        BookEntity book;
        for (int i = 0; i < NO_OF_QUOTES; i++) {
            book = new BookEntity("title_" + i, "author_" + i, LocalDate.now(), "cover_" + i);
            book.setFinished(i % 2 == 0);
            db.bookDao().create(book);
        }
    }

    @Test
    public void getById() {
        BookEntity result = db.bookDao().getById(1);

        assertEquals(1, result.getId());
        assertEquals("title_0", result.getTitle());
        assertEquals("author_0", result.getAuthor());
        assertEquals(LocalDate.now(), result.getAddedAt());
        assertEquals("cover_0", result.getCoverPath());
    }

    @Test
    public void getByTitle() {
        List<BookEntity> result = db.bookDao().getByTitle("title");
        assertEquals(NO_OF_QUOTES, result.size());
    }

    @Test
    public void getPage() {
        List<BookEntity> firstPage = db.bookDao().getPage(1, PAGE_SIZE);
        List<BookEntity> secondPage = db.bookDao().getPage(2, PAGE_SIZE);

        assertEquals(PAGE_SIZE, firstPage.size());
        assertEquals(NO_OF_QUOTES - PAGE_SIZE, secondPage.size());
    }

    @Test
    public void countByFinishedStatusTrue() {
        int result = db.bookDao().countByFinishedStatus(true);
        assertEquals(NO_OF_QUOTES / 2, result);
    }

    @Test
    public void countByFinishedStatusFalse() {
        int result = db.bookDao().countByFinishedStatus(false);
        assertEquals(NO_OF_QUOTES / 2, result);
    }

    @Test
    public void create() {
        BookEntity book = new BookEntity("new", "new_author", LocalDate.now(), "new_cover");
        long id = db.bookDao().create(book);

        assertEquals(NO_OF_QUOTES + 1, id);
    }

    @Test
    public void update() {
        BookEntity bookEntity = db.bookDao().getById(1);
        bookEntity.setTitle("another title");

        long result = db.bookDao().update(bookEntity);

        BookEntity updatedBookEntity = db.bookDao().getById(1);

        assertEquals(1, result);
        assertEquals(bookEntity.getId(), updatedBookEntity.getId());
        assertEquals(bookEntity.getTitle(), updatedBookEntity.getTitle());
    }

    @Test
    public void delete() {
        BookEntity bookEntity = db.bookDao().getById(1);
        long result = db.bookDao().delete(bookEntity);

        BookEntity updatedBookEntity = db.bookDao().getById(1);

        assertEquals(1, result);
        assertNull(updatedBookEntity);
    }

    @After
    public void tearDown() {
        db.close();
    }
}
