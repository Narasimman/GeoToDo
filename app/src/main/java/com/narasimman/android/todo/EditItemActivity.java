package com.narasimman.android.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        String task = getIntent().getStringExtra("task");
        long taskId = getIntent().getLongExtra("id", -1);
        id = taskId;
        EditText editItem = (EditText) findViewById(R.id.editText);
        editItem.setText(task);
    }

    public void onSubmit(View v) {
        Intent intent = new Intent();

        final EditText editItem = (EditText) findViewById(R.id.editText);
        intent.putExtra("task", editItem.getText().toString());
        intent.putExtra("id", id);
        setResult(RESULT_OK, intent);
        // closes the activity and returns to first screen
        this.finish();
    }
}
