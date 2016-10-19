package com.example.android.vinter_2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.vinter_2.data.DbContract;

/**
 * Created by Daniel Ibanez on 2016-10-19.
 */

public class TestCursorAdapter extends CursorAdapter {

    public TestCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * The newView method is used to inflate a new view and return it,
     * you don't bind any data to the view at this point.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_test, parent, false);
    }

    /**
     * The bindView method is used to bind all data to a given view
     * such as setting the text on a TextView.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvId = (TextView) view.findViewById(R.id.list_item_test_id);
        TextView tvPatId = (TextView) view.findViewById(R.id.list_item_test_patient_id);
        TextView tvCode = (TextView) view.findViewById(R.id.list_item_test_code);
        TextView tvContent = (TextView) view.findViewById(R.id.list_item_test_content);
        TextView tvStatus = (TextView) view.findViewById(R.id.list_item_test_status);
        TextView tvInOut = (TextView) view.findViewById(R.id.list_item_test_inout);


        // Extract properties from cursor
        int id = cursor.getInt(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_ID));
        int patId = cursor.getInt(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_PATIENT_ID_FK));
        String code = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CODE));
        String content = cursor.getString(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_CONTENT));
        int status = cursor.getInt(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_STATUS));
        int inOut = cursor.getInt(cursor.getColumnIndex(DbContract.TestEntry.COLUMN_INOUT));


        // Populate fields with extracted properties
        tvId.setText(String.valueOf(id));
        tvPatId.setText(String.valueOf(patId));
        tvCode.setText(code);
        tvContent.setText(content);
        tvStatus.setText(String.valueOf(status));
        tvInOut.setText(String.valueOf(inOut));
    }
}
