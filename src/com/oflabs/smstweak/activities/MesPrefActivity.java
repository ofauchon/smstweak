package com.oflabs.smstweak.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.oflabs.smstweak.R;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/3/11
 * Time: 6:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class MesPrefActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mesprefs);

    }
}