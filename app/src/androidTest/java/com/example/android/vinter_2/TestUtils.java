package com.example.android.vinter_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbContract.TestEntry;
import com.example.android.vinter_2.data.DbHelper;

import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Daniel Ibanez on 2016-10-07.
 */

@RunWith(AndroidJUnit4.class)
public class TestUtils {

    static void deleteDatabase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    static ContentValues createPatientValues(String name, int entry, String notes) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PatientEntry.COLUMN_NAME, name);
        values.put(PatientEntry.COLUMN_ENTRY_NUMBER, entry);
        values.put(PatientEntry.COLUMN_NOTES, notes);

        return values;
    }

    static ContentValues createTestValues(long patient_id, int date, String type, String content, int status) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TestEntry.COLUMN_PATIENT_ID_FK, patient_id);
        values.put(TestEntry.COLUMN_DATE, date);
        values.put(TestEntry.COLUMN_TYPE, type);
        values.put(TestEntry.COLUMN_CONTENT, content);
        values.put(TestEntry.COLUMN_STATUS, status);

        return values;
    }

    static Uri insertPatient(String name, int entry, String notes) {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        ContentValues values = createPatientValues(name, entry, notes);
        return appContext.getContentResolver().insert(PatientEntry.CONTENT_URI, values);
    }

    static Uri insertTest(long patient_id, int date, String type, String notes, int status) {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        ContentValues values = TestUtils.createTestValues(patient_id, date, type, notes, status);
        return appContext.getContentResolver().insert(TestEntry.CONTENT_URI, values);
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
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

}
