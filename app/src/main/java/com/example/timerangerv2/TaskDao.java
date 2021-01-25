package com.example.timerangerv2;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(com.example.timerangerv2.Task task);

    @Update
    void update(com.example.timerangerv2.Task task);

    @Delete
    void delete(com.example.timerangerv2.Task task);

    @Query("DELETE FROM tasks")
    void deleteAll();

    @Query("SELECT * FROM tasks ORDER BY title")
    LiveData<List<com.example.timerangerv2.Task>> findAll();

    @Query("SELECT * FROM tasks WHERE title like :title")
    List<com.example.timerangerv2.Task> findTaskWithName(String title);
}
