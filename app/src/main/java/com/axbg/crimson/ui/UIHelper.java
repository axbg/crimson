package com.axbg.crimson.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;


public class UIHelper {
    public static void hideKeyboard(FragmentActivity fragment, View view) {
        InputMethodManager inputManager = (InputMethodManager) fragment.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static View.OnFocusChangeListener getOutOfFocusListener(FragmentActivity fragment, View view) {
        return (listenerView, hasFocus) -> hideKeyboard(fragment, view);
    }
}
