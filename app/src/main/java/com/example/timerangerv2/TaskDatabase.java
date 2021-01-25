package com.example.timerangerv2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Task.class, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile com.example.timerangerv2.TaskDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static com.example.timerangerv2.TaskDatabase getInstance(final Context context)
    {
        if (INSTANCE == null)
        {
            synchronized (com.example.timerangerv2.TaskDatabase.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), com.example.timerangerv2.TaskDatabase.class, "task_db").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                TaskDao dao = INSTANCE.taskDao();
                dao.deleteAll();

                Task task = new Task("Zrobić pranie");
                dao.insert(task);
            });
        }
    };
}
