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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timerangerv2.R;
import com.example.timerangerv2.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView helloTextView;
    private FloatingActionButton addButton;
    private CheckBox doneCheckBox;
    private CheckBox importantCheckBox;
    private EditText title;
    private EditText description;
    private AlertDialog.Builder moreDialog;
    private DatabaseReference todoRef;
    private boolean completed;
    private boolean important;

    private DatabaseReference DATABASE = FirebaseDatabase.getInstance()
            .getReference("todos");

    LayoutInflater inflater;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getContext());
        helloTextView = view.findViewById(R.id.hello_text);
        recyclerView = view.findViewById(R.id.home_recycler_view_container);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = "http://10.0.2.2:5000/welcome";

        @SuppressLint("SetTextI18n") StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> { helloTextView.setText(response);
                                Log.d("DEBUG", String.valueOf(response));
                },
                error -> {
                    Log.d("DEBUG", error.getMessage());
                    Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    int currentTime = c.get(Calendar.HOUR_OF_DAY);
                    if (currentTime >= 6 && currentTime < 12)
                        helloTextView.setText("Good morning!");
                    else if (currentTime >= 12 && currentTime < 18)
                        helloTextView.setText("Good afternoon!");
                    else if (currentTime >= 18 && currentTime < 24)
                        helloTextView.setText("Good evening!");
                    else if (currentTime >= 0 && currentTime < 6)
                        helloTextView.setText("It is bed o'clock. You best be sleeping.");
                });

        queue.add(stringRequest);



        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                String key = null;
                String taskTitle = null;
                String desc = null;
                for (DataSnapshot e : snapshot.getChildren()) {
                    try {
                        completed = (boolean) e.child("isDone").getValue();
                        important = (boolean) e.child("important").getValue();
                        key = e.getKey();
                        taskTitle = e.child("title").getValue(String.class);
                        desc = e.child("description").getValue(String.class);
                    } catch (NullPointerException eee) {
                        continue;
                    }
                    Task taskToBeAdded = new Task(key, taskTitle, desc);
                    taskToBeAdded.setCompleted(completed);
                    taskToBeAdded.setImportant(important);
                    if (important) {
                        taskList.add(taskToBeAdded);
                    }
                }
                recyclerView.setAdapter(new TodoListAdapter(taskList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.home_recycler_view_container)
                        , "Task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class TodoListAdapter extends RecyclerView.Adapter<HomeFragment.TodoListAdapter.TodoListViewHolder> {
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
        public HomeFragment.TodoListAdapter.TodoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_list_element, parent, false);
            return new HomeFragment.TodoListAdapter.TodoListViewHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull HomeFragment.TodoListAdapter.TodoListViewHolder holder, int position) {
            holder.cardView.setCardForegroundColor(ColorStateList.valueOf(R.color.purple_100).withAlpha(35));
            holder.isDone.setChecked(this.todoList.get(position).isCompleted());
            holder.taskTitle.setText(this.todoList.get(position).getTitle());
            holder.taskDescription.setText(this.todoList.get(position).getDescription());

            holder.isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        Snackbar.make(recyclerView.findViewById(R.id.home_recycler_view_container),
                                "Task done!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(recyclerView.findViewById(R.id.home_recycler_view_container),
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
                            if (x.isImportant()) {
                                imp.setText("Important task!");
                            }
                        }
                    }

                    moreDialog.setPositiveButton("Mark as done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Task x : todoList
                            ) {
                                if (x.getTitle().contentEquals(holder.taskTitle.getText())) {
                                    x.setCompleted(!x.isCompleted());
                                    DATABASE.child(x.getTaskId()).child("isDone").setValue(x.isCompleted());
                                }
                            }
                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Task x : todoList
                            ) {
                                if (holder.taskTitle.toString().contentEquals(holder.taskTitle.getText())) {
                                    removeTask(x.getTaskId());
                                }
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