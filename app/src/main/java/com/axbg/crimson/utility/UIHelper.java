package com.axbg.crimson.utility;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;


public class UIHelper {
    public static void hideKeyboard(FragmentActivity fragmentActivity, View view) {
        InputMethodManager inputManager = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static View.OnFocusChangeListener getOutOfFocusListener(FragmentActivity fragmentActivity, View view) {
        return (listenerView, hasFocus) -> hideKeyboard(fragmentActivity, view);
    }

    public static void toggleView(boolean state, int layoutId, FragmentActivity fragmentActivity) {
        View toggledView = fragmentActivity.findViewById(layoutId);

        if (toggledView != null) {
            if (state) {
                toggledView.setVisibility(View.VISIBLE);
            } else {
                toggledView.setVisibility(View.GONE);
            }
        }
    }
}
