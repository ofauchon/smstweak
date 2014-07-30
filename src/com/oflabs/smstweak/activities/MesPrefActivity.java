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
