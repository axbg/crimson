package com.axbg.crimson;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.axbg.crimson.db.DatabaseManager;
import com.axbg.crimson.ui.books.BooksViewModel;
import com.axbg.crimson.ui.quotes.QuotesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        DatabaseManager.getInstance(getApplicationContext());

        bindNavigation();
        bindViewModels();
    }

    private void bindNavigation() {
        BottomNavigationView navView = findViewById(R.id.activity_landing_nav_view);
        NavController navController = Navigation.findNavController(this, R.id.activity_landing_nav_host_fragment);
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

        booksViewModel.getLiveDataBooks().observe(this, (books) -> {
            if (books.size() == 0) {
                Navigation.findNavController(this, R.id.activity_landing_nav_host_fragment)
                        .navigate(R.id.navigation_books);
            }
            booksViewModel.getLiveDataBooks().removeObservers(this);
        });
    }
}
