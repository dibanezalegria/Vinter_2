package com.example.android.vinter_2.data;

import android.provider.BaseColumns;

/**
 * Created by Daniel Ibanez on 2016-10-05.
 */

public class DbContract {

    /**
     * Inner class that defines the table contents of the patient table
     */
    public static final class PatientEntry implements BaseColumns {
        public static final String TABLE_NAME = "patient";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ENTRY_NUMBER = "entry_number";
        public static final String COLUMN_NOTES = "notes";
    }

    /**
     * Inner class that defines the table contents of the test table
     */
    public static final class TestEntry implements BaseColumns {
        public static final String TABLE_NAME = "test";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PATIENT_ID_FK = "patient_id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DATE = "date";
    }
}
