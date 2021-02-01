package com.example.timerangerv2.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.timerangerv2.MainActivity;
import com.example.timerangerv2.R;
import com.example.timerangerv2.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;


public class TodoFragment extends Fragment {

    private DatabaseReference DATABASE = FirebaseDatabase.getInstance()
            .getReference("todos");

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private CheckBox doneCheckBox;
    private CheckBox importantCheckBox;
    private EditText title;
    private EditText description;
    private AlertDialog.Builder addDialog;
    private AlertDialog.Builder moreDialog;
    private DatabaseReference todoRef;
    private boolean completed;
    private boolean important;

    LayoutInflater inflater;

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
        inflater = LayoutInflater.from(getContext());
        recyclerView = view.findViewById(R.id.todo_recycler_view_container);
        addButton = view.findViewById(R.id.add_todo_button);
        addDialog = new AlertDialog.Builder(getContext());

        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                String key = null;
                String title = null;
                String desc = null;
                for (DataSnapshot e : snapshot.getChildren()) {
                    try {
                        completed = (boolean) e.child("isDone").getValue();
                        important = (boolean) e.child("important").getValue();
                        key = e.getKey();
                        title = e.child("title").getValue(String.class);
                        desc = e.child("description").getValue(String.class);
                    } catch (NullPointerException eee) {
                        continue;
                    }
                    Task taskToBeAdded = new Task(key, title, desc);
                    taskToBeAdded.setCompleted(completed);
                    taskToBeAdded.setImportant(important);
                    taskList.add(taskToBeAdded);
                }
                recyclerView.setAdapter(new TodoListAdapter(taskList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                title = view.findViewById(R.id.task_title);
                description = view.findViewById(R.id.task_description);
                View dialogView = inflater.inflate(R.layout.add_task_dialog, null);

                addDialog.setView(dialogView);

                addDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String titleValue = title.getText().toString();
                        String descriptionValue = description.getText().toString();
                        boolean importantValue = importantCheckBox.isChecked();
                        todoRef = DATABASE.push();
                        todoRef.child("isDone").setValue(false);
                        todoRef.child("important").setValue(importantValue);
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
                importantCheckBox = dialogView.findViewById(R.id.task_important_check_box);
                addDialog.setTitle("Add new To-do task");
                addDialog.show();
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

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull TodoListViewHolder holder, int position) {
            holder.isDone.setChecked(this.todoList.get(position).isCompleted());
            holder.taskTitle.setText(this.todoList.get(position).getTitle());
            holder.taskDescription.setText(this.todoList.get(position).getDescription());
            for (Task x: todoList)
            {
                if (x.getTitle().contentEquals(holder.taskTitle.getText()))
                {
                    if(x.isImportant())
                    {
                        holder.cardView.setCardForegroundColor(ColorStateList.valueOf(R.color.purple_100).withAlpha(35));
                    }
                    else
                    {
                        holder.cardView.setCardForegroundColor(ColorStateList.valueOf(R.color.purple_100).withAlpha(20));
                    }
                }
            }

            holder.isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                                "Task done!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                                "Task undone.", Snackbar.LENGTH_SHORT).show();
                    }

                    for (Task x : todoList
                    ) {
                        if (x.getTitle().contentEquals(holder.taskTitle.getText())) {
                            x.setCompleted(!x.isCompleted());
                            DATABASE.child(x.getTaskId()).child("isDone").setValue(x.isCompleted());
                        }
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = inflater.inflate(R.layout.more_info_task_dialog, null);
                    moreDialog = new AlertDialog.Builder(getContext());
                    moreDialog.setView(dialogView);
                    TextView desc = dialogView.findViewById(R.id.more_info_description);
                    TextView imp = dialogView.findViewById(R.id.more_info_important);
                    for (Task x : todoList
                    ) {
                        if (x.getTitle().contentEquals(holder.taskTitle.getText())) {
                            desc.setText(x.getDescription());
                            if (x.isImportant()){
                                imp.setText("Important task!");
                            }
                        }
                    }

                    moreDialog.setPositiveButton("Mark as done", (dialog, which) -> {
                        for (Task x : todoList
                        ) {
                            if (x.getTitle().contentEquals(holder.taskTitle.getText())) {
                                x.setCompleted(!x.isCompleted());
                                DATABASE.child(x.getTaskId()).child("isDone").setValue(x.isCompleted());
                            }
                        }
                    }).setNegativeButton("Delete", (dialog, which) -> {
                        for (Task x : todoList
                        ) {
                            if (x.getTitle().contentEquals(holder.taskTitle.getText())) {
                                removeTask(x.getTaskId());
                                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container),
                                        "Task deleted!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }).setNeutralButton("Cancel", (dialog, which) -> {

                    }).setTitle(holder.taskTitle.getText().toString()).create();
                    moreDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return todoList.size();
        }

        private class TodoListViewHolder extends RecyclerView.ViewHolder {
            private final MaterialCardView cardView;
            private final CheckBox isDone;
            private final TextView taskTitle;
            private final TextView taskDescription;

            public TodoListViewHolder(@NonNull View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.todo_list_card_view);

                isDone = itemView.findViewById(R.id.todo_list_element_is_done);
                taskTitle = itemView.findViewById(R.id.todo_list_element_title);
                taskDescription = itemView.findViewById(R.id.todo_list_element_description);
            }
        }
    }

    public static void removeTask(String name) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("todos");
        dbRef.child(name).removeValue();
    }
}