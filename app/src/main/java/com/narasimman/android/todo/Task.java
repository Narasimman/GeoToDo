package com.narasimman.android.todo;

/**
 * Created by nsairam on 8/17/17.
 */

public class Task {
    private String task;
    private long id;

    public Task(final String task) {
        this.task = task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
