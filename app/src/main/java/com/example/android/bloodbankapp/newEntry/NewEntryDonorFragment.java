package com.example.android.bloodbankapp.newEntry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bloodbankapp.R;
import com.example.android.bloodbankapp.data.BloodBankContract;
import com.example.android.bloodbankapp.data.BloodBankDbHelper;
import com.example.android.bloodbankapp.data.TestUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by jaihi on 3/27/2017.
 */

public class NewEntryDonorFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText editTextDonorName, editTextContactNo, editTextQuantity, editTextPrice;
    Spinner spinnerBloodgroup;
    Button buttonSave;

    SQLiteDatabase mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_entry_donor, container, false);
        editTextDonorName = (EditText) view.findViewById(R.id.et_dname);
        editTextContactNo = (EditText) view.findViewById(R.id.et_dphone);
        editTextQuantity = (EditText) view.findViewById(R.id.et_dquantity);
        editTextPrice = (EditText) view.findViewById(R.id.et_dprice);
        spinnerBloodgroup = (Spinner) view.findViewById(R.id.sp_bldgrp);
        buttonSave = (Button) view.findViewById(R.id.btn_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String name = editTextDonorName.getText().toString();
                    String contactNo = editTextContactNo.getText().toString();
                    Integer quantity = Integer.parseInt(editTextQuantity.getText().toString());
                    Integer price = Integer.parseInt(editTextPrice.getText().toString());
                    String bloodGroup = spinnerBloodgroup.getSelectedItem().toString();
                    BloodBankDbHelper dbHelper = new BloodBankDbHelper(getActivity());
                    mDb = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(BloodBankContract.TransactionEntry.COLUMN_NAME, name);
                    values.put(BloodBankContract.TransactionEntry.COLUMN_CONTACT_NO, contactNo);
                    values.put(BloodBankContract.TransactionEntry.COLUMN_TYPE, getString(R.string.actor_type_donor));
                    values.put(BloodBankContract.TransactionEntry.COLUMN_BLOOD_GROUP, bloodGroup);
                    values.put(BloodBankContract.TransactionEntry.COLUMN_QUANTITY, quantity);
                    values.put(BloodBankContract.TransactionEntry.COLUMN_PRICE, price);
                    addDonor(values);
                    Toast.makeText(getContext(), "Data Saved", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "Please Enter Proper Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void addDonor(ContentValues values) {

        final String sDateSelection =
                BloodBankContract.DateEntry.TABLE_NAME +
                        "." + BloodBankContract.DateEntry.COLUMN_DAY + " = ? AND " +
                        BloodBankContract.DateEntry.TABLE_NAME +
                        "." + BloodBankContract.DateEntry.COLUMN_MONTH + " = ? AND " +
                        BloodBankContract.DateEntry.TABLE_NAME +
                        "." + BloodBankContract.DateEntry.COLUMN_YEAR + " = ?";

        String[] sDateSelectionArgs = getDate();

        Cursor dateCursor = mDb.query(BloodBankContract.DateEntry.TABLE_NAME,
                new String[] {BloodBankContract.DateEntry._ID},
                sDateSelection,
                sDateSelectionArgs,
                null,
                null,
                null
        );
        long dateId;
        if (!dateCursor.moveToFirst()) {
            ContentValues dateValues=new ContentValues();
            dateValues.put(BloodBankContract.DateEntry.COLUMN_DAY, sDateSelectionArgs[0]);
            dateValues.put(BloodBankContract.DateEntry.COLUMN_MONTH, sDateSelectionArgs[1]);
            dateValues.put(BloodBankContract.DateEntry.COLUMN_YEAR, sDateSelectionArgs[2]);
            dateId = mDb.insert(BloodBankContract.DateEntry.TABLE_NAME, null, dateValues);
        } else {
            dateId = dateCursor.getInt(dateCursor.getColumnIndex(BloodBankContract.DateEntry._ID));
        }
        values.put(BloodBankContract.TransactionEntry.COLUMN_DATE_KEY, dateId);
        long rowId = mDb.insert(BloodBankContract.TransactionEntry.TABLE_NAME, null, values);
    }

    private String[] getDate() {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = df.format(calender.getTime());
        return currentDate.split("-");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}