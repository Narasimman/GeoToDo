package com.narasimman.android.todo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by nsairam on 8/17/17.
 */

public class Task implements Serializable{
    private String task;
    private long id;
    private Priority priority;
    private Date due;

    enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

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

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setDue(Date date) {
        this.due = date;
    }

    public Date getDue() {
        return due;
    }
}
