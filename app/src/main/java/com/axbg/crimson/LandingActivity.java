package com.axbg.crimson;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.db.entity.QuoteEntity;
import com.axbg.crimson.ui.books.BooksViewModel;
import com.axbg.crimson.ui.quotes.QuotesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;

public class LandingActivity extends AppCompatActivity {
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        databaseManager = DatabaseManager.getInstance(getApplicationContext());

//        addDummyData();
        bindNavigation();
        bindViewModels();
    }

    private void bindNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void bindViewModels() {
        BooksViewModel booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        QuotesViewModel quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        quotesViewModel.setGetBookByIdLambda(aVoid -> booksViewModel.getBooksHashMap());
        try {
            quotesViewModel.loadQuotes();
        } catch (Exception ignored) {
        }
    }

    private void addDummyData() {
        databaseManager.bookDao().getAll().observe(this, (books) -> {
            if (books.size() == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            databaseManager.quoteDao().create(new QuoteEntity("Quote" + i, LocalDate.now(), 6));
                        }
                    }
                }.start();
            }
        });
    }
}
