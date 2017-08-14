package com.narasimman.android.todo;

import android.content.ContentValues;
import android.content.Intent;
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
        items = new ArrayList<>();
        readFromDB();
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemsAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdaptor);
        setupListViewListener();
    }

    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String item = etNewItem.getText().toString();
        if (item.length() < 1) {
            return;
        }
        itemsAdaptor.add(item);
        etNewItem.setText("");
        saveToDB(items.size() - 1, item);
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdaptor.notifyDataSetChanged();
                        deleteItemOnDB(pos);
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                        launchComposeView(pos, items.get(pos));
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String task = data.getStringExtra("task");
            int id = data.getIntExtra("id", 0);
            items.set(id, task);
            updateItemOnDB(id, task);
            itemsAdaptor.notifyDataSetChanged();
        }
    }

    public void launchComposeView(final int id, final String task) {
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("task", task);
        startActivityForResult(intent, 200);
    }

    private void saveToDB(final int pos, final String task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task);
        values.put(SQLiteDBHelper.COLUMN_POS, pos);
        long newRowId = database.insert(SQLiteDBHelper.TABLE_NAME, null, values);

        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        final SQLiteDatabase database = new SQLiteDBHelper(this).getReadableDatabase();

        final String[] projection = {
                SQLiteDBHelper.COLUMN_ID,
                SQLiteDBHelper.COLUMN_POS,
                SQLiteDBHelper.COLUMN_TASK
        };

        try (Cursor cursor = database.query(
                    SQLiteDBHelper.TABLE_NAME,   // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // don't sort
            )) {

            while (cursor.moveToNext()) {
                items.set(cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_POS)), cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_TASK)));
            }
        }
    }

    private void updateItemOnDB(final int pos, final String task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task);
        database.update(SQLiteDBHelper.TABLE_NAME, values, SQLiteDBHelper.COLUMN_POS + "=" + pos, null);

        Toast.makeText(this, "Item Updated.", Toast.LENGTH_LONG).show();
    }

    private void deleteItemOnDB(final int pos) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        database.delete(SQLiteDBHelper.TABLE_NAME, SQLiteDBHelper.COLUMN_POS + "=" + pos, null);

        Toast.makeText(this, "Item deleted.", Toast.LENGTH_LONG).show();
    }
}
