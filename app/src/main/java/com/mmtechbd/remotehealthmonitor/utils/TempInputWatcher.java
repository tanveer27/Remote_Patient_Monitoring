package com.mmtechbd.remotehealthmonitor.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

/**
 * Created by Roaim on 30-Nov-16.
 */

public class TempInputWatcher implements TextWatcher {
    Button button;

    public TempInputWatcher(Button button) {
        this.button = button;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String input = editable.toString();
        if (input.isEmpty()) button.setEnabled(false);
        else {
            int temp = Integer.parseInt(input);
            if (temp>50 && temp<120) button.setEnabled(true);
            else button.setEnabled(false);
        }
    }
}
