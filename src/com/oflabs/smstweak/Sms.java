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

import android.telephony.SmsMessage;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/11/11
 * Time: 11:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sms {
    private int id;
    private String callerid;
    private String body;
    private long timestamp;

    public Sms() {
    }

    public Sms(String callerid, String body) {
        this.callerid = callerid;
        this.body = body;
    }

    public Sms(SmsMessage m){
        callerid = m.getOriginatingAddress();
        body = m.getMessageBody();
        timestamp = m.getTimestampMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallerid() {
        return callerid;
    }

    public void setCallerid(String callerid) {
        this.callerid = callerid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "ID : " + id + "\nCallerID : " + callerid + "\nBody : " + body;
    }

}
