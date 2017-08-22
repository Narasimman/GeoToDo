package com.narasimman.android.todo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nsairam on 8/17/17.
 */

public class TasksAdapter extends ArrayAdapter<Task> {
    private List<Task> tasks;
    public TasksAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }
        // Lookup view for data population
        TextView taskItem = (TextView) convertView.findViewById(R.id.task_text);
        TextView taskPriority = (TextView) convertView.findViewById(R.id.task_priority);
        TextView taskDue = (TextView) convertView.findViewById(R.id.due_date);
        // Populate the data into the template view using the data object
        taskItem.setText(task.getTask());
        taskPriority.setText(task.getPriority().name());

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        taskDue.setText(sdf.format(task.getDue()));

        taskItem.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Task task = getItem(position);
                        remove(task);
                        ((MainActivity) getContext()).deleteItemOnDB(task);
                        return true;
                    }
                }
        );

        taskItem.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Task task = getItem(position);
                        ((MainActivity) getContext()).launchComposeView(task);
                    }
                }
        );
        // Return the completed view to render on screen
        return convertView;
    }
}
