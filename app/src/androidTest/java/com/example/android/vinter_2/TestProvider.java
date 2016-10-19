package com.example.android.vinter_2;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.android.vinter_2.data.DbContract;
import com.example.android.vinter_2.data.DbContract.PatientEntry;
import com.example.android.vinter_2.data.DbContract.TestEntry;
import com.example.android.vinter_2.data.DbProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.android.vinter_2.TestUtils.insertPatient;
import static com.example.android.vinter_2.TestUtils.insertTest;
import static org.junit.Assert.*;

/**
 * Created by Daniel Ibanez on 2016-10-07.
 */
@RunWith(AndroidJUnit4.class)
public class TestProvider {

    private static final String LOG_TAG = TestProvider.class.getSimpleName();

    private Context mContext;

    /**
     * Since we want each test to start with a clean slate, run deleteAllRecords in setUp
     * (called by the test runner before each test).
     */
    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDeleteAllRecordsFromDb() {
        // Delete first tests due to foreign key constraints
        mContext.getContentResolver().delete(TestEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(PatientEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(PatientEntry.CONTENT_URI,
                null, null, null, null, null);

        assertEquals("Error: Records not deleted from Patient table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(TestEntry.CONTENT_URI,
                null, null, null, null, null);

        assertEquals("Error: Records not deleted from Test table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /**
     * This test checks to make sure that the content provider is registered correctly.
     */
    @Test
    public void testProviderRegistry() {
        PackageManager packageManager = mContext.getPackageManager();

        // Define the component name based on the package name from the context and the DbProvider class
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                DbProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: DbProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + DbContract.CONTENT_AUTHORITY,
                    providerInfo.authority, DbContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /**
     * Test query
     */
    @Test
    public void testQuery() {
        // Delete records before test
        testDeleteAllRecordsFromDb();

        // Query Patient table
        Cursor cursor = mContext.getContentResolver().query(PatientEntry.CONTENT_URI, null, null, null, null, null);

        if (cursor != null) {
            assertEquals("Error: query failed at testQuery", 0, cursor.getCount());
            cursor.close();
        } else {
            fail("Error: query failed at testQuery");
        }

        // Query Test table
        cursor = mContext.getContentResolver().query(TestEntry.CONTENT_URI, null, null, null, null, null);

        if (cursor != null) {
            assertEquals("Error: query failed at testQuery", 0, cursor.getCount());
            cursor.close();
        } else {
            fail("Error: query failed at testQuery");
        }
    }

    /**
     * Test insert patient/test
     */
    @Test
    public void testInsert() {
        // Delete records before test
        testDeleteAllRecordsFromDb();

        // Insert patient
        Uri newPatientUri = insertPatient("Daniel", 100, "TestProvider");
        long id = ContentUris.parseId(newPatientUri);

        assertEquals("Error: inserting patient at testInsert",
                ContentUris.withAppendedId(PatientEntry.CONTENT_URI, id),   // expected URI
                newPatientUri);

        // Insert test
        Uri newTestUri = insertTest(ContentUris.parseId(newPatientUri), 1971, "IMF", "daniel's content", 1, 0);
        id = ContentUris.parseId(newTestUri);

        assertEquals("Error: inserting test at testInsert",
                ContentUris.withAppendedId(TestEntry.CONTENT_URI, id),   // expected URI
                newTestUri);
    }

    /**
     * Test deleting records
     */
    @Test
    public void testDelete() {
        // Delete records before test
        testDeleteAllRecordsFromDb();

        // Insert patient
        Uri newPatientUri = TestUtils.insertPatient("Daniel", 500, "daniel notes");
        Log.d(LOG_TAG, "newPatientUri: " + newPatientUri);
        Uri expectedPatientUri = ContentUris.withAppendedId(PatientEntry.CONTENT_URI, ContentUris.parseId(newPatientUri));

        assertEquals("Error: insert patient failed at testDelete", expectedPatientUri, newPatientUri);

        // Insert test
        Uri newTestUri = TestUtils.insertTest(ContentUris.parseId(newPatientUri), 1971, "IMF", "daniel's content", 1, 0);
        Uri expectedTestUri = ContentUris.withAppendedId(TestEntry.CONTENT_URI, ContentUris.parseId(newTestUri));

        assertEquals("Error: insert test failed at testDelete", newTestUri, expectedTestUri);

        // Delete test first due to foreign key constraints.
        // Only patients with no tests in test table can be deleted.
        int testsDeleted = mContext.getContentResolver().delete(newTestUri, null, null);

        assertEquals("Error: deleting test at testDelete", 1, testsDeleted);

        // Delete patient
        int patientsDeleted = mContext.getContentResolver().delete(newPatientUri, null, null);

        assertEquals("Error: deleting patient at testDelete", 1, patientsDeleted);
    }

    @Test
    public void update() {
        // Delete records before test
        testDeleteAllRecordsFromDb();

        // Update patient

        // Update test
    }

}
