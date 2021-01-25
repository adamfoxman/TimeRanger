package com.example.timerangerv2.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.timerangerv2.MainActivity;
import com.example.timerangerv2.R;
import com.example.timerangerv2.Task;
import com.example.timerangerv2.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TodoFragment extends Fragment {

    private DatabaseReference DATABASE = FirebaseDatabase.getInstance()
            .getReference("todos");

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.todo_recycler_view_container);
        addButton = view.findViewById(R.id.add_todo_button);
        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                snapshot.getChildren().forEach(e -> {
                    taskList.add(new Task(e.child("title").getValue(String.class), "testowy"));
                });
                recyclerView.setAdapter(new TodoListAdapter(taskList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        // TODO: 1/25/21 zmienić z tej listy na bazę danych
//        List<Task> todoList = new ArrayList<>();
//        todoList.add(new Task("Make laundry", "Black clothes"));
//        todoList.add(new Task("Go for a walk", "5km"));
//        recyclerView.setAdapter(new TodoListAdapter(todoList, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DatabaseReference todoRef = DATABASE.child("2");
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                todoRef.child("title").setValue("jest dzik");
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Task added to list!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListViewHolder> {
        List<Task> todoList;
        Context mContext;

        private static final String TAG = "TodoListAdapter";

        public TodoListAdapter(List<Task> todoList, Context context) {
            this.todoList = todoList;
            this.mContext = context;
            Log.d(TAG, "Created adapter with " + todoList.toString());
        }

        @NonNull
        @Override
        public TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_list_element, parent, false);
            return new TodoListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoListViewHolder holder, int position) {
            holder.isDone.setChecked(this.todoList.get(position).isCompleted());
            holder.taskTitle.setText(this.todoList.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return todoList.size();
        }

        private class TodoListViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox isDone;
            private final TextView taskTitle;

            public TodoListViewHolder(@NonNull View itemView) {
                super(itemView);

                isDone = itemView.findViewById(R.id.todo_list_element_is_done);
                taskTitle = itemView.findViewById(R.id.todo_list_element_title);
            }
        }
    }
}