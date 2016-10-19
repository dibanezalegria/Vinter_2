package com.example.android.vinter_2;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.vinter_2.data.DbContract.PatientEntry;

/**
 * Created by Daniel Ibanez on 2016-10-19.
 */

public class PatientCursorAdapter extends CursorAdapter {

    public PatientCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * The newView method is used to inflate a new view and return it,
     * you don't bind any data to the view at this point.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_patient, parent, false);
    }

    /**
     * The bindView method is used to bind all data to a given view
     * such as setting the text on a TextView.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvId = (TextView) view.findViewById(R.id.list_item_patient_id);
        TextView tvName = (TextView) view.findViewById(R.id.list_item_patient_name);
        TextView tvEntry = (TextView) view.findViewById(R.id.list_item_patient_entry);
        TextView tvNotes = (TextView) view.findViewById(R.id.list_item_patient_notes);

        // Extract properties from cursor
        int id = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_NAME));
        int entry = cursor.getInt(cursor.getColumnIndex(PatientEntry.COLUMN_ENTRY_NUMBER));
        String notes = cursor.getString(cursor.getColumnIndex(PatientEntry.COLUMN_NOTES));

        // Populate fields with extracted properties
        tvId.setText(String.valueOf(id));
        tvName.setText(name);
        tvEntry.setText(String.valueOf(entry));
        tvNotes.setText(notes);
    }
}
