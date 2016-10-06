package com.example.android.vinter_2;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.vinter_2.data.DbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DbHelper dbHelper = DbHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Database is open");
    }
}
