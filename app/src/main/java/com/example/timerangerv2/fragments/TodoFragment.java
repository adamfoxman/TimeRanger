package com.example.timerangerv2.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.timerangerv2.R;
import com.example.timerangerv2.Task;
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
    private CheckBox doneCheckBox;
    private EditText title;
    private EditText description;
    private AlertDialog.Builder dialog;
    private DatabaseReference todoRef;

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
        dialog = new AlertDialog.Builder(getContext());

        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                snapshot.getChildren().forEach(e -> {
                    taskList.add(new Task(e.getKey(), e.child("title").getValue(String.class), e.child("description").getValue(String.class)));
                });
                recyclerView.setAdapter(new TodoListAdapter(taskList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addButton.setOnClickListener(new View.OnClickListener() {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
//                todoRef.child("title").setValue("jest dzik");
                title = view.findViewById(R.id.task_title);
                description = view.findViewById(R.id.task_description);
                View dialogView = inflater.inflate(R.layout.add_task_dialog, null);

                dialog.setView(dialogView);

                dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String titleValue = title.getText().toString();
                        String descriptionValue = description.getText().toString();
                        todoRef = DATABASE.push();
                        todoRef.child("title").setValue(titleValue);
                        todoRef.child("description").setValue(descriptionValue);
                        Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                                "Task added to list!", Snackbar.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                                "Task not added.", Snackbar.LENGTH_LONG).show();
                    }
                }).create();
                title = dialogView.findViewById(R.id.task_title);
                description = dialogView.findViewById(R.id.task_description);
                dialog.setTitle("Add new To-do task");
                dialog.show();
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
            holder.taskDescription.setText(this.todoList.get(position).getDescription());
            String idKey = null;
            holder.isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                            "Task done!", Snackbar.LENGTH_SHORT).show();
                    for (Task x:todoList
                         ) {
                        if (x.getTitle().contentEquals(holder.taskTitle.getText()))
                        {
                            x.setCompleted(true);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return todoList.size();
        }

        private class TodoListViewHolder extends RecyclerView.ViewHolder {
            private final CheckBox isDone;
            private final TextView taskTitle;
            private final TextView taskDescription;

            public TodoListViewHolder(@NonNull View itemView) {
                super(itemView);

                isDone = itemView.findViewById(R.id.todo_list_element_is_done);
                taskTitle = itemView.findViewById(R.id.todo_list_element_title);
                taskDescription = itemView.findViewById(R.id.todo_list_element_description);
            }
        }
    }
}