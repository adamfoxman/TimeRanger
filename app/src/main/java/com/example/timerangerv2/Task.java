package com.example.timerangerv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

;import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

@Entity(tableName = "tasks")
public class Task implements DatabaseReference.CompletionListener {
    @PrimaryKey(autoGenerate = true)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String taskId;
    private String title;
    private String description;
    private boolean daily;
    private boolean positive;
    private boolean negative;
    private boolean completed;
    private boolean important;

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public Task(String title)
    {
        this.title = title;
        this.daily = false;
        this.positive = false;
        this.negative = false;
        this.completed = false;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", daily=" + daily +
                ", positive=" + positive +
                ", negative=" + negative +
                ", completed=" + completed +
                '}';
    }

    @Ignore
    public Task(String taskId, String title, String description)
    {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.daily = false;
        this.positive = false;
        this.negative = false;
        this.completed = false;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDaily() {
        return daily;
    }

    public void setDaily(boolean daily) {
        this.daily = daily;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isHabit() { return positive || negative; }

    public boolean isTodo() { return !daily && !positive && !negative; }

    @Override
    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
        System.out.println("XD");
    }
}
