package com.axbg.crimson.ui.quotes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QuotesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QuotesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is quotes fragment");
    }

    LiveData<String> getText() {
        return mText;
    }
}