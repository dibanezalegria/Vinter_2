package com.example.android.vinter_2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbContract.TestEntry;


/**
 * Created by Daniel Ibanez on 2016-10-07.
 */

public class DbProvider extends ContentProvider {

    private final static String LOG_TAG = DbProvider.class.getSimpleName();

    /*
     * Constants used by UriMatcher
     */
    private static final int PATIENTS = 100;
    private static final int PATIENT_ID = 101;
    private static final int TESTS = 200;
    private static final int TEST_ID = 201;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Add URI patterns that the provider should recognize.
        sUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_PATIENT, PATIENTS);
        sUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_PATIENT + "/#", PATIENT_ID);

        sUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_TEST, TESTS);
        sUriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_TEST + "/#", TEST_ID);
    }

    // Database helper object
    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = DbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                cursor = database.query(PatientEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PATIENT_ID:
                selection = PatientEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PatientEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TESTS:
                cursor = database.query(TestEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TEST_ID:
                selection = TestEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TestEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return insertPatient(uri, values);
            case TESTS:
                return insertTest(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Helper method that inserts row in patient table
     */
    private Uri insertPatient(Uri uri, ContentValues values) {
        // TODO: catch exception in MainActivity
        // Data validation
        String name = values.getAsString(PatientEntry.COLUMN_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Exception in insertPatient (DbProvider): " +
                    "Patient requires a name");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PatientEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the patient URI
        // uri: content://com.example.android.vinter_2/patient
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Helper method that inserts row in test table
     */
    private Uri insertTest(Uri uri, ContentValues values) {
        // Data validation
        // TODO: validate values (patient_id, code, status, inout)
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Foreign key constraint throws exception if a patient with id = patient_id
        // is not found in table 'patient'
        long id;
        try {
            id = db.insertOrThrow(TestEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException ex) {
            Log.d(LOG_TAG, "DbProvider.insertTest() throws SQLiteConstraintException");
            return null;
        }

        if (id == -1) {
            Log.d(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the patient URI
        // uri: content://com.example.android.vinter_2/test
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted = 0;
        final int match = sUriMatcher.match(uri);
        try {
            switch (match) {
                case PATIENTS:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case PATIENT_ID:
                    // Delete a single row given by the ID in the URI
                    selection = PatientEntry.COLUMN_ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = db.delete(PatientEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case TESTS:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = db.delete(TestEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case TEST_ID:
                    // Delete a single row given by the ID in the URI
                    selection = TestEntry.COLUMN_ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = db.delete(TestEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }
        } catch (SQLiteConstraintException ex) {
            Log.d(LOG_TAG, "DbProvider.delete() throws SQLiteConstraintException");
        }

        // Notify listeners
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return updatePatient(uri, values, selection, selectionArgs);
            case PATIENT_ID:
                selection = PatientEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePatient(uri, values, selection, selectionArgs);
            case TESTS:
                return updateTest(uri, values, selection, selectionArgs);
            case TEST_ID:
                selection = TestEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTest(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Helper method. Updates rows in table patient
     */
    private int updatePatient(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // TODO: validate provided values

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(PatientEntry.TABLE_NAME, values, selection, selectionArgs);
        // Notify listeners
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Helper method. Updates rows in table test.
     */
    private int updateTest(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // TODO: validate provided values

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(TestEntry.TABLE_NAME, values, selection, selectionArgs);
        // Notify listeners
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Implement this [method] to handle requests for the MIME type of the data at the given URI.
     * The returned MIME type should start with “vnd.android.cursor.item” for a single record,
     * or “vnd.android.cursor.dir/” for multiple items.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PATIENTS:
                return PatientEntry.CONTENT_LIST_TYPE;
            case PATIENT_ID:
                return PatientEntry.CONTENT_ITEM_TYPE;
            case TESTS:
                return TestEntry.CONTENT_LIST_TYPE;
            case TEST_ID:
                return TestEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
