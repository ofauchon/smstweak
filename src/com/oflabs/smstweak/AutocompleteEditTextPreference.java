/*
Copyright (C) 2013, 2014 Olivier Fauchon
This file is part of SMS-TWEAK Android Aplication.

SMS-TWEAK is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SMS-TWEAK is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
*/

package com.oflabs.smstweak;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 10/20/11
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class AutocompleteEditTextPreference extends EditTextPreference {



    public AutocompleteEditTextPreference(Context context) {
        super(context);
    }

    public AutocompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public AutocompleteEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    /**
     * the default EditTextPreference does not make it easy to
     * use an AutoCompleteEditTextPreference field. By overriding this method
     * we perform surgery on it to use the type of edit field that
     * we want.
     */
    protected void onBindDialogView(View view) {
      super.onBindDialogView(view);

        // find the current EditText object from the EditTextPreference, and do the surgery
        final EditText editText = (EditText) view.findViewById(android.R.id.edit);

        // copy its layout params & remove it from the layout
        ViewGroup.LayoutParams params = editText.getLayoutParams();
        ViewGroup vg = (ViewGroup) editText.getParent();
        String curVal = editText.getText().toString();
        vg.removeView(editText);
        // construct a new editable autocomplete object with the appropriate params
        // and id that the TextEditPreference is expecting
        mACTV = new MultiAutoCompleteTextView(getContext());
        mACTV.setTokenizer(new MyTokenizer());
        mACTV.setLayoutParams(params);
        mACTV.setId(android.R.id.edit);
        mACTV.setText(curVal);
        mACTV.setThreshold(0);



        String[] toks= new String[] {};
        if (getKey().startsWith("rule"))
            toks = new String[]{"message", "callerid", "equals", "contains", "icontains", "and", "or"};
        else if (getKey().startsWith("action"))
            toks = new String[]{"play", "voice", "vibrate", "push"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_multiple_choice,
                toks);
        mACTV.setAdapter(adapter);

        // add the new view to the layout
        vg.addView(mACTV);
    }

    /**
     * Because the baseclass does not handle this correctly
     * we need to query our injected AutoCompleteTextView for
     * the value to save
     */
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mACTV != null) {
            String value = mACTV.getText().toString();
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }

    /**
     * again we need to override methods from the base class
     */
    public EditText getEditText() {
        return mACTV;
    }

    private MultiAutoCompleteTextView mACTV = null;
    private final String TAG = "AutocompleteEditTextPreference";
}

class MyTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && text.charAt(i - 1) != ' ') {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == ' ') {
                return i;
            } else {
                i++;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ' ') {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + " ");
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return text + " ";
            }
        }
    }
}
