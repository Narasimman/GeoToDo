package com.narasimman.android.todo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> items;
    ArrayAdapter<String> itemsAdaptor;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<String>();
        readFromDB();
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemsAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdaptor);
        setupListViewListener();
    }

    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String item = etNewItem.getText().toString();
        itemsAdaptor.add(item);
        etNewItem.setText("");
        saveToDB(item);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdaptor.notifyDataSetChanged();
                        //deleteItemOnDB();
                        return true;
                    }
                }
        );
    }

    private void saveToDB(final String task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task);
        long newRowId = database.insert(SQLiteDBHelper.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        final SQLiteDatabase database = new SQLiteDBHelper(this).getReadableDatabase();

        final String[] projection = {
                SQLiteDBHelper.COLUMN_ID,
                SQLiteDBHelper.COLUMN_TASK
        };

        Cursor cursor = null;
        try {
            cursor = database.query(
                    SQLiteDBHelper.TABLE_NAME,   // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // don't sort
            );

            while (cursor.moveToNext()) {
                items.add(cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_TASK)));
            }
        } finally {
            cursor.close();
        }
    }

    private void deleteItemOnDB(final int id) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        long newRowId = database.delete(SQLiteDBHelper.TABLE_NAME, SQLiteDBHelper.COLUMN_ID + "=" + id, null);

        Toast.makeText(this, "Item deleted.", Toast.LENGTH_LONG).show();
    }
}
