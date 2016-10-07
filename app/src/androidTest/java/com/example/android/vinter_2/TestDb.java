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

import static com.example.android.vinter_2.TestUtils.validateCurrentRecord;
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
    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    @Test
    public void setUp() {
        TestUtils.deleteDatabase();
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

    @Test
    public void insertPatientValues() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        // insert our test records into the database
        DbHelper dbHelper = DbHelper.getInstance(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtils.createPatientValues("Daniel", 1971, "Some notes");

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
