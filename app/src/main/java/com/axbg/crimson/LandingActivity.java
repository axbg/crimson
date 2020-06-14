package com.axbg.crimson;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.ui.books.BooksViewModel;
import com.axbg.crimson.ui.quotes.QuotesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.function.Function;

public class LandingActivity extends AppCompatActivity {

    private QuotesViewModel quotesViewModel;
    private BooksViewModel booksViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        bindNavigation();
        bindViewModels();
    }

    private void bindNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void bindViewModels() {
        booksViewModel = new ViewModelProvider(this).get(BooksViewModel.class);
        quotesViewModel = new ViewModelProvider(this).get(QuotesViewModel.class);
        quotesViewModel.setBookByIdLambda(new Function<Long, BookEntity>() {
            @Override
            public BookEntity apply(Long bookId) {
                return booksViewModel.getBook(bookId);
            }
        });
    }
}
