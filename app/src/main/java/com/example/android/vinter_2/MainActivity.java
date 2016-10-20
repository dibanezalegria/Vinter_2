package com.example.android.vinter_2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbContract.TestEntry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Loader constants
    private static final int PATIENT_LOADER = 0;
    private static final int TEST_LOADER = 1;

    private ListView mListViewPatients, mListViewTests;
    private PatientCursorAdapter mPatientCursorAdapter;
    private TestCursorAdapter mTestCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find fields to populate in inflated template
        Button btnAddPatient = (Button) findViewById(R.id.btn_add_dummy_patient);
        Button btnAddTest = (Button) findViewById(R.id.btn_add_dummy_test);
        Button btnUpdatePatient = (Button) findViewById(R.id.btn_update_patient);
        Button btnUpdateTest = (Button) findViewById(R.id.btn_update_test);
        Button btnDeletePatient = (Button) findViewById(R.id.btn_delete_patient);
        Button btnDeleteTest = (Button) findViewById(R.id.btn_delete_test);
        Button btnQuery = (Button) findViewById(R.id.btn_query);
        final EditText etQueryPatientID = (EditText) findViewById(R.id.et_query_patient_id);
        final EditText etQueryCode = (EditText) findViewById(R.id.et_query_test_code);
        final EditText etQueryInOut = (EditText) findViewById(R.id.et_query_test_inout);

        final EditText etPatientID = (EditText) findViewById(R.id.et_patient_id);
        final EditText etTestID = (EditText) findViewById(R.id.et_test_id);
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
                        // TODO: cath parseInt exception
                        Integer.parseInt(tvEntry.getText().toString()),
                        tvNotes.getText().toString());
            }
        });

        btnAddTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyTestForPatient(Integer.parseInt(tvID.getText().toString()),
                        tvCode.getText().toString(),
                        tvContent.getText().toString(),
                        // TODO: catch parseInt exception
                        Integer.parseInt(tvStatus.getText().toString()),
                        Integer.parseInt(tvInOut.getText().toString()));
            }
        });

        btnUpdatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatient(Integer.parseInt(etPatientID.getText().toString()),
                        tvName.getText().toString(),
                        // TODO: cath parseInt exception
                        Integer.parseInt(tvEntry.getText().toString()),
                        tvNotes.getText().toString());
            }
        });

        btnUpdateTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTest(Integer.parseInt(etTestID.getText().toString()),
                        Integer.parseInt(tvID.getText().toString()),
                        tvCode.getText().toString(),
                        tvContent.getText().toString(),
                        // TODO: catch parseInt exception
                        Integer.parseInt(tvStatus.getText().toString()),
                        Integer.parseInt(tvInOut.getText().toString()));
            }
        });

        btnDeletePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePatient(Integer.parseInt(etPatientID.getText().toString()));
            }
        });

        btnDeleteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTest(Integer.parseInt(etTestID.getText().toString()));
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int patientID = 0;
                if (etQueryPatientID.getText().length() != 0) {
                    patientID = Integer.parseInt(etQueryPatientID.getText().toString());
                }

                String code = null;
                if (etQueryCode.getText().length() != 0) {
                    code = etQueryCode.getText().toString();
                }

                int inout = -1;
                if (etQueryInOut.getText().length() != 0) {
                    inout = Integer.parseInt(etQueryInOut.getText().toString());
                }

                Cursor cursor = query(patientID, code, inout);
                Log.d(LOG_TAG, "cursor: " + cursor.getCount());

                while(cursor.moveToNext()) {
                    int testID = cursor.getInt(cursor.getColumnIndex(TestEntry.COLUMN_ID));
                    Log.d(LOG_TAG, "TestID: " + testID);
                }

                cursor.close();
            }
        });

        // List views
        mListViewPatients = (ListView) findViewById(R.id.list_view_patients);
        mListViewTests = (ListView) findViewById(R.id.list_view_tests);

        // Custom cursor adapters
        // There is no patient/test data yet (until the loader finishes) so pass in null as cursors
        mPatientCursorAdapter = new PatientCursorAdapter(this, null);
        mTestCursorAdapter = new TestCursorAdapter(this, null);

        // Setup adapters
        mListViewPatients.setAdapter(mPatientCursorAdapter);
        mListViewTests.setAdapter(mTestCursorAdapter);

        // Kick off the loader
        getSupportLoaderManager().initLoader(PATIENT_LOADER, null, this);
        getSupportLoaderManager().initLoader(TEST_LOADER, null, this);
    }

    /**
     * Add patient to database and return uri
     */
    private Uri addDummyPatient(String name, int entryNum, String notes) {
        // Insert a new patient in patient table
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entryNum);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        Uri uri = null;
        try {
            uri = getContentResolver().insert(PatientEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "Insert patient returned uri: " + uri.toString());
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, e.getMessage());
        }

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

    private int updatePatient(int patientID, String name, int entryNum, String notes) {
        // Uri to update
        Uri uri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, patientID);

        // Values to update
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entryNum);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        // Update patient
        int rowsUpdated = getContentResolver().update(uri, values, null, null);
        Toast.makeText(this, "Rows updated: " + rowsUpdated, Toast.LENGTH_SHORT).show();
        return rowsUpdated;
    }

    private int updateTest(int testID, int patientId, String code, String content, int status, int inout) {
        // Uri to update
        Uri uri = ContentUris.withAppendedId(TestEntry.CONTENT_URI, testID);

        // Values to update
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_CONTENT, content);
        values.put(TestEntry.COLUMN_STATUS, status);
        values.put(TestEntry.COLUMN_INOUT, inout);

        // Update patient
        int rowsUpdated = getContentResolver().update(uri, values, null, null);
        Toast.makeText(this, "Rows updated: " + rowsUpdated, Toast.LENGTH_SHORT).show();
        return rowsUpdated;
    }

    private int deletePatient(int patientID) {
        int rowsDeleted;
        if (patientID == 0) {
            // Delete all rows
            rowsDeleted = getContentResolver().delete(PatientEntry.CONTENT_URI, null, null);
        } else {
            // Uri to delete
            Uri uri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, patientID);
            // Delete
            rowsDeleted = getContentResolver().delete(uri, null, null);
        }
        Toast.makeText(this, "Patients deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
        return rowsDeleted;
    }

    private int deleteTest(int testID) {
        int rowsDeleted;
        if (testID == 0) {
            // Delete all rows
            rowsDeleted = getContentResolver().delete(TestEntry.CONTENT_URI, null, null);
        } else {
            // Uri to delete
            Uri uri = ContentUris.withAppendedId(TestEntry.CONTENT_URI, testID);
            // Delete
            rowsDeleted = getContentResolver().delete(uri, null, null);
        }
        Toast.makeText(this, "Tests deleted: " + rowsDeleted, Toast.LENGTH_SHORT).show();
        return rowsDeleted;
    }

    private Cursor query(int patientID, String code, int inout) {
        ArrayList<String> listSelectionArgs = new ArrayList<>();
        StringBuilder builderSelection = new StringBuilder();
        if (inout != -1) {
            builderSelection.append(TestEntry.COLUMN_INOUT + "=?");
            listSelectionArgs.add(String.valueOf(inout));
        }

        if (code != null) {
            if (builderSelection.length() != 0) {
                builderSelection.append(" AND ");
            }
            builderSelection.append(TestEntry.COLUMN_CODE + "=?");
            listSelectionArgs.add(code);
        }

        if (patientID != 0) {
            if (builderSelection.length() != 0) {
                builderSelection.append(" AND ");
            }
            builderSelection.append(TestEntry.COLUMN_PATIENT_ID_FK + "=?");
            listSelectionArgs.add(String.valueOf(patientID));
        }

        String selection = builderSelection.toString();
        String[] selectionArgs = listSelectionArgs.toArray(new String[listSelectionArgs.size()]);

        Log.d(LOG_TAG, "selection: " + selection + " args: " + selectionArgs.toString());


        // All rows from table 'test' where patientID and code and status
//        String selection = TestEntry.COLUMN_ID + "=? AND " + TestEntry.COLUMN_CODE + "=? AND " +
//                TestEntry.COLUMN_STATUS + "=?";
//        String[] selectionArgs = new String[]{String.valueOf(patientID),
//                code, String.valueOf(status)};
        return getContentResolver().query(TestEntry.CONTENT_URI, null, selection,
                selectionArgs, null);
    }

    /**
     * LoaderManager callbacks interface methods
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PATIENT_LOADER:
                return new CursorLoader(this,
                        PatientEntry.CONTENT_URI, null, null, null, null);
            case TEST_LOADER:
                return new CursorLoader(this,
                        TestEntry.CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update adapters with new cursor containing updated data
        switch (loader.getId()) {
            case PATIENT_LOADER:
                mPatientCursorAdapter.swapCursor(data);
                break;
            case TEST_LOADER:
                mTestCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        switch (loader.getId()) {
            case PATIENT_LOADER:
                mPatientCursorAdapter.swapCursor(null);
                break;
            case TEST_LOADER:
                mTestCursorAdapter.swapCursor(null);
        }
    }

}
