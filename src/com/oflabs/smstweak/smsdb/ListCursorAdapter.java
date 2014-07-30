package com.oflabs.smstweak.smsdb;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.oflabs.smstweak.R;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/17/11
 * Time: 12:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListCursorAdapter extends CursorAdapter {

    public ListCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public ListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_sms, viewGroup, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
        TextView tvCallerId = (TextView) view.findViewById(R.id.tvCallerId);
        TextView tvDateTime= (TextView) view.findViewById(R.id.tvDateTime);
        tvBody.setText(cursor.getString(cursor.getColumnIndex("body")));
        tvCallerId.setText("From:" + cursor.getString(cursor.getColumnIndex("callerid")));
        tvDateTime.setText("Date:" + cursor.getString(cursor.getColumnIndex("datetime")));

        //To change body of implemented methods use File | Settings | File Templates.
    }
}
