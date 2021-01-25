package com.example.timerangerv2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    private LiveData<List<Task>> tasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        tasks = taskRepository.findAllTasks();
    }

    public LiveData<List<Task>> findAll()
    {
        return tasks;
    }

    public void insert(Task task)
    {
        taskRepository.insert(task);
    }

    public void update(Task task)
    {
        taskRepository.update(task);
    }

    public void delete(Task task)
    {
        taskRepository.delete(task);
    }
}
