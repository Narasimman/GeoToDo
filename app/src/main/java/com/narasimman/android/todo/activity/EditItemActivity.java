package com.narasimman.android.todo.activity;

import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.narasimman.android.todo.fragment.DatePickerFragment;
import com.narasimman.android.todo.R;
import com.narasimman.android.todo.model.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Task task = (Task) getIntent().getExtras().getSerializable("task");
        final String taskString = task.getTask();
        final String priority = task.getPriority().name();
        id = task.getId();
        EditText editItem = (EditText) findViewById(R.id.editText);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        TextView dueText = (TextView) findViewById(R.id.date_text);

        editItem.setText(taskString);

        //TODO:
        Task.Priority[] priority_values = Task.Priority.values();
        List<String> prioList = new ArrayList<>();
        for (Task.Priority p : priority_values) {
            prioList.add(p.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, prioList);
        spinner.setAdapter(adapter);
        spinner.setSelection(prioList.indexOf(priority));

        final Date dueDate = task.getDue();
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dueText.setText(sdf.format(dueDate));
    }

    public void onSubmit(View v) {
        Intent intent = new Intent(this, Task.class);

        final EditText editItem = (EditText) findViewById(R.id.editText);
        Task task = new Task(editItem.getText().toString());
        task.setId(id);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        task.setPriority(Task.Priority.valueOf(spinner.getSelectedItem().toString()));

        TextView dueText = (TextView) findViewById(R.id.date_text);
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        try {
            final Date dueDate = (Date) formatter.parse(dueText.getText().toString());
            task.setDue(dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        intent.putExtra("task", task);
        setResult(RESULT_OK, intent);
        // closes the activity and returns to first screen
        this.finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
