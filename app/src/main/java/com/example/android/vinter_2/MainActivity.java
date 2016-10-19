package com.example.android.vinter_2;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbContract.TestEntry;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ListView mListViewPatients, mListViewTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find fields to populate in inflated template
        Button btnAddPatient = (Button) findViewById(R.id.btn_add_dummy_patient);
        Button btnAddTest = (Button) findViewById(R.id.btn_add_dummy_test);
        final TextView tvName = (TextView) findViewById(R.id.list_item_patient_name);
        final TextView tvEntry = (TextView) findViewById(R.id.list_item_patient_entry);
        final TextView tvNotes = (TextView) findViewById(R.id.list_item_patient_notes);
        final TextView tvID = (TextView) findViewById(R.id.list_item_patient_id);
        final TextView tvCode = (TextView) findViewById(R.id.list_item_test_code);
        final TextView tvContent = (TextView) findViewById(R.id.list_item_test_content);
        final TextView tvStatus = (TextView) findViewById(R.id.list_item_test_status);
        final TextView tvInOut = (TextView) findViewById(R.id.list_item_test_inout);

        // Listeners
        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyPatient(tvName.getText().toString(),
                        Integer.parseInt(tvEntry.getText().toString()),
                        tvNotes.getText().toString());

                // TODO: this update will be automatic after implementing cursor loaders
                updateListViewPatients();
            }
        });

        btnAddTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyTestForPatient(Integer.parseInt(tvID.getText().toString()),
                        tvCode.getText().toString(),
                        tvContent.getText().toString(),
                        Integer.parseInt(tvStatus.getText().toString()),
                        Integer.parseInt(tvInOut.getText().toString()));

                // TODO: this update will be automatic after implementing cursor loaders
                updateListViewTests();
            }
        });

        // List views
        mListViewPatients = (ListView) findViewById(R.id.list_view_patients);
        mListViewTests = (ListView) findViewById(R.id.list_view_tests);

        // Show list contents
        // TODO: these updates will be automatic after implementing cursor loaders
        updateListViewPatients();
        updateListViewTests();
    }

    /**
     *  Add patient to database and return uri
     */
    private Uri addDummyPatient(String name, int entryNum, String notes) {
        // Insert a new patient in patient table
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entryNum);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        Uri uri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Insert patient returned uri: " + uri.toString());

        return uri;
    }

    /**
     * Add test to database for given patient id
     */
    private Uri addDummyTestForPatient(int patientId, String code, String content, int status, int inout) {
        // Insert a new test for given patient id
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_PATIENT_ID_FK, patientId);
        values.put(TestEntry.COLUMN_CODE, code);
        values.put(TestEntry.COLUMN_CONTENT, content);
        values.put(TestEntry.COLUMN_STATUS, status);
        values.put(TestEntry.COLUMN_INOUT, inout);

        Uri uri = getContentResolver().insert(TestEntry.CONTENT_URI, values);
        Log.d(LOG_TAG, "Insert patient returned uri: " + uri);

        return uri;
    }

    /**
     * Update patient list view
     */
    public void updateListViewPatients() {
        // Query for all rows in patient table
        Cursor cursor = getContentResolver().query(PatientEntry.CONTENT_URI, null, null, null, null);
        // Setup custom adapter using returned cursor from query
        PatientCursorAdapter adapter = new PatientCursorAdapter(this, cursor);
        // Attach cursor adapter to list view
        mListViewPatients.setAdapter(adapter);
    }

    /**
     * Update test list view
     */
    public void updateListViewTests() {
        // Query for all rows in test table
        Cursor cursor = getContentResolver().query(TestEntry.CONTENT_URI, null, null, null, null);
        // Setup custom adapter using returned cursor from query
        TestCursorAdapter adapter = new TestCursorAdapter(this, cursor);
        // Attach cursor adapter to list view
        mListViewTests.setAdapter(adapter);
    }
}
