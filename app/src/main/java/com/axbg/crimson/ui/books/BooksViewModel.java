package com.axbg.crimson.ui.books;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

public class BooksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BooksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is books fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}