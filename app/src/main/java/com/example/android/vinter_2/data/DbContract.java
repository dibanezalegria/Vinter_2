package com.example.android.vinter_2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Daniel Ibanez on 2016-10-05.
 */

public class DbContract {
    //  Content Provider constants
    public static final String CONTENT_AUTHORITY = "com.example.android.vinter_2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PATIENT = "patient";
    public static final String PATH_TEST = "test";

    /**
     * Inner class that defines the table contents of the patient table
     */
    public static final class PatientEntry implements BaseColumns {
        //  The content URI to access the patient data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PATIENT);

        public static final String TABLE_NAME = "patient";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ENTRY_NUMBER = "entry_number";
        public static final String COLUMN_NOTES = "notes";

        // MIME types used by the getType method ContentProvider
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATIENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PATIENT;
    }

    /**
     * Inner class that defines the table contents of the test table
     */
    public static final class TestEntry implements BaseColumns {
        //  The content URI to access the patient data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TEST);

        public static final String TABLE_NAME = "test";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PATIENT_ID_FK = "patient_id";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_INOUT = "inout";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DATE = "date";

        // MIME types used by the getType method ContentProvider
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;

    }
}
