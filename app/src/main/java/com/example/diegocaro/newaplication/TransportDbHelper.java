package com.example.diegocaro.newaplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by diego.caro on 11/03/2016.
 */
public class TransportDbHelper extends SQLiteOpenHelper {

    // private properties
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TransportEntry.TABLE_NAME + " (" +
                    TransportEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
                    TransportEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    TransportEntry.COLUMN_NAME_PHONE + TEXT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TransportEntry.TABLE_NAME;


    // Database version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Transport.db";

    public TransportDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int olderVersion, int newVersion)
    {
        onUpgrade(db, olderVersion, newVersion);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // adding new transport
    public void addTransport(Transport transport) {
        // for logging
        Log.d("addTransport", transport.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TransportEntry.COLUMN_NAME_PHONE, transport.getPhone());
        values.put(TransportEntry.COLUMN_NAME_NAME, transport.getName());


        //Insert row
        long newRowId = db.insert(TransportEntry.TABLE_NAME, null, values);
        db.close();
    }

    // getting single transport
    public Transport getTransport(int transportId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from database you will actually use
        //after this query.
        String[] projection = {
                TransportEntry.COLUMN_NAME_ENTRY_ID,
                TransportEntry.COLUMN_NAME_NAME,
                TransportEntry.COLUMN_NAME_PHONE
        };

        String selection = TransportEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = {
                String.valueOf(transportId)
        };

        //How you want the results sorted in the resulting Cursor
        String sortOrder = TransportEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                TransportEntry.TABLE_NAME,      // The table to query
                projection,                     // The columns to return
                selection,                      // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                           // don't group the rows
                null,                           // don't filter by rows groups
                sortOrder                       // the sort order
        );

        if (cursor != null)
            cursor.moveToFirst();

        Transport transport = new Transport();
        transport.setID(Integer.parseInt(cursor.getString(0)));
        transport.setName(cursor.getString(1));
        transport.setPhone(cursor.getString(2));

        //log
        Log.d("getTransport(" + transportId + ")", transport.toString());

        return transport;
    }

    // getting all transports
    public List<Transport> getAllTransport() {
        List<Transport> transportList = new LinkedList<Transport>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TransportEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        Transport transport = null;

        if (cursor.moveToFirst()) {
            do {
                transport = new Transport();
                transport.setID(Integer.parseInt(cursor.getString(0)));
                transport.setName(cursor.getString(1));
                transport.setPhone(cursor.getString(2));

                transportList.add(transport);
            } while (cursor.moveToNext());
        }

        Log.d("getAllTransport", transportList.toString());

        return transportList;
    }

    // Updating single transport
    public int updateTransport(Transport transport) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TransportEntry.COLUMN_NAME_PHONE, transport.getPhone());
        contentValues.put(TransportEntry.COLUMN_NAME_NAME, transport.getName());

        // which row to update, based on the ID
        String selection = TransportEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(transport.getID())};

        int count = db.update(
                TransportEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs
        );

        return count;
    }

    // deleting single transport
    public void deleteTransport(Transport transport) {
        SQLiteDatabase db = this.getWritableDatabase();

        // define 'where' part of query
        String selection = TransportEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";

        // specify arguments in placeholder order
        String[] selectionArgs = { String.valueOf(transport.getID())};

        // issue SQL statement
        db.delete(TransportEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    // getting transports count
    public int getTransportCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TransportEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return cursor.getCount();
    }


    // Scheme class
    public static abstract class TransportEntry implements BaseColumns {
        public static final String TABLE_NAME = "Transports";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
    }
}
