package com.example.timerangerv2;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> tasks;

    TaskRepository(Application application)
    {
        TaskDatabase database = TaskDatabase.getInstance(application);
        taskDao = database.taskDao();
        tasks = taskDao.findAll();
    }

    LiveData<List<Task>> findAllTasks()
    {
        return tasks;
    }

    void insert(Task task)
    {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    void update(Task task)
    {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    void delete(Task task)
    {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }
}
