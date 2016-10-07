package com.example.android.vinter_2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbHelper;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database helper
        DbHelper dbHelper = DbHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Database is open");

        // Insert a new patient in patient table
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, "Daniel");
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, 100);
        values.put(PatientEntry.COLUMN_NOTES, "New patient");

        Uri uri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Insert patient returned uri: " + uri.toString());

        // Query database for all rows in patient table via ContentResolver -> ContentProvider
        Cursor cursor = getContentResolver().query(PatientEntry.CONTENT_URI, null, null, null, null);

        Log.d(LOG_TAG, "cursor count: " + cursor.getCount());
    }
}
