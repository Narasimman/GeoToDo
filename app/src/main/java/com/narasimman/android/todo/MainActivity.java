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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        intent.putExtra("task", task);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Task task = (Task) data.getSerializableExtra("task");
            Task updatedTask = null;
            for (Task t : items) {
                if (t.getId() == task.getId()) {
                    updatedTask = t;
                    break;
                }
            }
            updatedTask.setTask(task.getTask());
            updatedTask.setPriority(task.getPriority());
            updatedTask.setDue(task.getDue());
            updateItemOnDB(updatedTask);
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
        task.setPriority(Task.Priority.HIGH);

        Date today = Calendar.getInstance().getTime();
        task.setDue(today);
        long id = saveToDB(task);
        task.setId(id);
        items.add(task);
        etNewItem.setText("");
        itemsAdaptor.notifyDataSetChanged();
    }

    private long saveToDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task.getTask());
        values.put(SQLiteDBHelper.COLUMN_PRIORITY, task.getPriority().name());
        values.put(SQLiteDBHelper.COLUMN_DUE, new SimpleDateFormat("MM/dd/yy").format(task.getDue()));
        long newRowId = database.insert(SQLiteDBHelper.TABLE_NAME, null, values);
        Toast.makeText(this, "The new Row Id is " + newRowId, Toast.LENGTH_LONG).show();
        return newRowId;
    }


    private void updateItemOnDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteDBHelper.COLUMN_TASK, task.getTask());
        values.put(SQLiteDBHelper.COLUMN_PRIORITY, task.getPriority().name());
        values.put(SQLiteDBHelper.COLUMN_DUE, new SimpleDateFormat("MM/dd/yy").format(task.getDue()));
        database.update(SQLiteDBHelper.TABLE_NAME, values, SQLiteDBHelper.COLUMN_ID + "=" + task.getId(), null);

        Toast.makeText(this, "Item Updated.", Toast.LENGTH_LONG).show();
    }

    private void readFromDB() {
        final SQLiteDatabase database = new SQLiteDBHelper(this).getReadableDatabase();

        final String[] projection = {
                SQLiteDBHelper.COLUMN_ID,
                SQLiteDBHelper.COLUMN_TASK,
                SQLiteDBHelper.COLUMN_PRIORITY,
                SQLiteDBHelper.COLUMN_DUE
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
                task.setPriority(Task.Priority.valueOf(cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_PRIORITY))));

                final String dateStr = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COLUMN_DUE));
                DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                final Date dueDate = (Date)formatter.parse(dateStr);
                task.setDue(dueDate);
                items.add(task);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void deleteItemOnDB(final Task task) {
        SQLiteDatabase database = new SQLiteDBHelper(this).getWritableDatabase();
        database.delete(SQLiteDBHelper.TABLE_NAME, SQLiteDBHelper.COLUMN_ID + "=" + task.getId(), null);
        Toast.makeText(this, "Item deleted.", Toast.LENGTH_LONG).show();
    }
}
