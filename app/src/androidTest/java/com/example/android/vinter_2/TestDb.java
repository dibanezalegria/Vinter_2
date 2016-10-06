package com.example.android.vinter_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.vinter_2.data.DbContract;
import com.example.android.vinter_2.data.DbHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestDb {

    private void deleteDatabase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    @Test
    public void setUp() {
        deleteDatabase();
    }

    @Test
    public void testCreateDatabase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(DbContract.PatientEntry.TABLE_NAME);
        tableNameHashSet.add(DbContract.TestEntry.TABLE_NAME);

        appContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = DbHelper.getInstance(appContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while( cursor.moveToNext() );

        // if this fails, it means that your database doesn't contain both the patient entry
        // and test entry tables
        assertTrue("Error: Your database was created without both the patient entry and test entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + DbContract.PatientEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> patientColumnHashSet = new HashSet<String>();
        patientColumnHashSet.add(DbContract.PatientEntry.COLUMN_ID);
        patientColumnHashSet.add(DbContract.PatientEntry.COLUMN_NAME);
        patientColumnHashSet.add(DbContract.PatientEntry.COLUMN_ENTRY_NUMBER);
        patientColumnHashSet.add(DbContract.PatientEntry.COLUMN_NOTES);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            patientColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required patient entry columns",
                patientColumnHashSet.isEmpty());

        cursor.close();
        db.close();
    }

    private ContentValues createPatientValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(DbContract.PatientEntry.COLUMN_NAME, "NameTest");
        testValues.put(DbContract.PatientEntry.COLUMN_ENTRY_NUMBER, 1971);
        testValues.put(DbContract.PatientEntry.COLUMN_NOTES, "Notes Test");

        return testValues;
    }

    private void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    @Test
    public void insertPatientValues() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // insert our test records into the database
        DbHelper dbHelper = DbHelper.getInstance(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createPatientValues();

        long locationRowId;
        locationRowId = db.insert(DbContract.PatientEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(DbContract.PatientEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Move the cursor to a valid database row
        assertTrue("Error: no records returned from patient query", cursor.moveToFirst());

        validateCurrentRecord("Error: Patient query validation failed", cursor, testValues);

        assertFalse("Error: More than one record returned from patient query", cursor.moveToNext());

        cursor.close();
        db.close();
    }

}
