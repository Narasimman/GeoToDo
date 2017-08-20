package com.narasimman.android.todo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Task> items;
    ArrayAdapter<Task> itemsAdaptor;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items = new ArrayList<>();
        readFromDB();
        itemsAdaptor = new TasksAdapter(this, items);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdaptor);

        Button button = (Button) findViewById(R.id.btnAddItem);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddItem(v);
            }
        });
    }

    public void launchComposeView(final Task task) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("id", task.getId());
        intent.putExtra("task", task.getTask());
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String taskStr = data.getStringExtra("task");
            long id = data.getLongExtra("id", -1);
            Task task = null;
            for (Task t : items) {
                if (t.getId() == id) {
                    task = t;
                    break;
                }
            }
            task.setTask(taskStr);
            updateItemOnDB(task);
            itemsAdaptor.notifyDataSetChanged();
        }
    }

    public void onAddItem(View view) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String item = etNewItem.getText().toString();
        if (item.length() < 1) {
            return;
        }
        Task task = new Task(item);
        etNewItem.setText("");
        long id = saveToDB(task);
        task.setId(id);
        items.add(task);
        itemsAdaptor.notifyDataSetChanged();
    }

    private long saveToDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task.getTask());
        long newRowId = database.insert(SQLiteDBHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
        return newRowId;
    }


    private void updateItemOnDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task.getTask());
        database.update(SQLiteDBHelper.TABLE_NAME, values, SQLiteDBHelper.COLUMN_ID + "=" + task.getId(), null);

        Toast.makeText(this, "Item Updated.", Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        final SQLiteDatabase database = new SQLiteDBHelper(this).getReadableDatabase();

        final String[] projection = {
                SQLiteDBHelper.COLUMN_ID,
                SQLiteDBHelper.COLUMN_TASK
        };

        try (Cursor cursor = database.query(
                SQLiteDBHelper.TABLE_NAME,   // The table to query
                projection,                  // The columns to return
                null,                        // The columns for the WHERE clause
                null,                        // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                null                         // don't sort
        )) {

            while (cursor.moveToNext()) {
                Task task = new Task(cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_TASK)));
                items.add(task);
            }
        }
    }

    public void deleteItemOnDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        database.delete(SQLiteDBHelper.TABLE_NAME, SQLiteDBHelper.COLUMN_ID + "=" + task.getId(), null);
        Toast.makeText(this, "Item deleted.", Toast.LENGTH_LONG).show();
    }
}
