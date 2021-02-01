package com.example.timerangerv2.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;
import java.util.List;

public class DailyFragment extends Fragment {
    private DatabaseReference DATABASE = FirebaseDatabase.getInstance().getReference("dailies");

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private CheckBox doneCheckBox;
    private EditText title;
    private EditText description;
    private RadioButton positiveRadioButton;
    private RadioButton negativeRadioButton;
    private AlertDialog.Builder addDialog;
    private AlertDialog.Builder moreDialog;
    private DatabaseReference todoRef;
    private boolean positive;
    private boolean negative;

    LayoutInflater inflater;

    public DailyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater = LayoutInflater.from(getContext());
        recyclerView = view.findViewById(R.id.daily_recycler_view_container);
        addButton = view.findViewById(R.id.add_daily_button);
        addDialog = new AlertDialog.Builder(getContext());

        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> dailyList = new ArrayList<>();

                for(DataSnapshot e: snapshot.getChildren()) {
                    try {
                        positive = (boolean) e.child("positive").getValue();
                        negative = (boolean) e.child("negative").getValue();
                        String key = e.getKey();
                        String title = e.child("title").getValue(String.class);
                        String desc = e.child("description").getValue(String.class);
                        boolean completed = (boolean) e.child("isDone").getValue();
                        Task dailyToBeAdded = new Task(key, title, desc);
                        dailyToBeAdded.setCompleted(completed);
                        dailyToBeAdded.setPositive(positive);
                        dailyToBeAdded.setNegative(negative);
                        dailyList.add(dailyToBeAdded);
                    } catch (NullPointerException eee) {
                        continue;
                    }
                }
                recyclerView.setAdapter(new DailyListAdapter(dailyList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Daily task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dailyRef.child("title").setValue("fajne zadanie");
//                dailyRef.child("description").setValue("fajny opis");
                title = view.findViewById(R.id.habit_title);
                description = view.findViewById(R.id.habit_description);
                View dialogView = inflater.inflate(R.layout.add_habit_dialog, null);

                addDialog.setView(dialogView);

                addDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String titleValue = title.getText().toString();
                        String descriptionValue = description.getText().toString();
                        boolean positiveValue = positiveRadioButton.isChecked();
                        boolean negativeValue = negativeRadioButton.isChecked();
                        todoRef = DATABASE.push();
                        todoRef.child("isDone").setValue(false);
                        todoRef.child("title").setValue(titleValue);
                        todoRef.child("description").setValue(descriptionValue);
                        todoRef.child("positive").setValue(positiveValue);
                        todoRef.child("negative").setValue(negativeValue);
                        Snackbar.make(recyclerView.findViewById(R.id.daily_recycler_view_container),
                                "Habit added to list!", Snackbar.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Snackbar.make(recyclerView.findViewById(R.id.daily_recycler_view_container),
                                "Habit not added.", Snackbar.LENGTH_LONG).show();
                    }
                }).create();
                title = dialogView.findViewById(R.id.habit_title);
                description = dialogView.findViewById(R.id.habit_description);
                positiveRadioButton = dialogView.findViewById(R.id.habit_positive_check_box);
                negativeRadioButton = dialogView.findViewById(R.id.habit_negative_check_box);
                addDialog.setTitle("Add new habit");
                addDialog.show();
            }
        });
    }

    public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.DailyListHolder>
    {
        List<Task> dailyList;
        Context mContext;

        private static final String TAG = "DailyListAdapter";

        public DailyListAdapter(List<Task> dailyList, Context context)
        {
            this.dailyList = dailyList;
            this.mContext = context;
        }

        @NonNull
        @Override
        public DailyListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_list_element, parent, false);
            return new DailyListHolder(view);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull DailyListHolder holder, int position) {
            holder.isDone.setChecked(this.dailyList.get(position).isCompleted());
            holder.habitTitle.setText(this.dailyList.get(position).getTitle());
            holder.habitDescription.setText(this.dailyList.get(position).getDescription());
            for (Task x: dailyList)
            {
                if (x.getTitle().contentEquals(holder.habitTitle.getText()))
                {
                    if(x.isPositive() && !x.isNegative())
                    {
                        holder.cardView.setCardForegroundColor(ColorStateList.valueOf(Color.GREEN).withAlpha(20));
                    }
                    else if (x.isNegative() && !x.isPositive())
                    {
                        holder.cardView.setCardForegroundColor(ColorStateList.valueOf(Color.RED).withAlpha(20));
                    }
                }
            }

            holder.isDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        Snackbar.make(recyclerView.findViewById(R.id.daily_recycler_view_container),
                                "Task done!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(recyclerView.findViewById(R.id.daily_recycler_view_container),
                                "Task undone.", Snackbar.LENGTH_SHORT).show();
                    }

                    for (Task x : dailyList
                    ) {
                        if (x.getTitle().contentEquals(holder.habitTitle.getText())) {
                            x.setCompleted(!x.isCompleted());
                            DATABASE.child(x.getTaskId()).child("isDone").setValue(x.isCompleted());
                        }
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                @Override
                public void onClick(View v) {
                    View dialogView = inflater.inflate(R.layout.more_info_task_dialog, null);
                    moreDialog = new AlertDialog.Builder(getContext());
                    moreDialog.setView(dialogView);
                    TextView desc = dialogView.findViewById(R.id.more_info_description);
                    TextView imp = dialogView.findViewById(R.id.more_info_important);
                    for (Task x : dailyList
                    ) {
                        if (x.getTitle().contentEquals(holder.habitTitle.getText())) {
                            desc.setText(x.getDescription());
                            if (x.isPositive() && !x.isNegative())
                                imp.setText("Positive habit");
                            else if (!x.isPositive() && x.isNegative())
                                imp.setText("Negative habit");
                        }
                    }

                    moreDialog.setPositiveButton("Mark as done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Task x : dailyList
                            ) {
                                if (x.getTitle().contentEquals(holder.habitTitle.getText())) {
                                    x.setCompleted(!x.isCompleted());
                                    DATABASE.child(x.getTaskId()).child("isDone").setValue(x.isCompleted());
                                }
                            }
                        }
                    }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Task x : dailyList
                            ) {
                                if (holder.habitTitle.toString().contentEquals(holder.habitTitle.getText())) {
                                    removeHabit(x.getTaskId());
                                }
                            }
                        }
                    }).setNeutralButton("Cancel", (dialog, which) -> {

                    }).setTitle(holder.habitTitle.getText().toString()).create();
                    moreDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dailyList.size();
        }

        private class DailyListHolder extends RecyclerView.ViewHolder {
            private final MaterialCardView cardView;
            private final CheckBox isDone;
            private final TextView habitTitle;
            private final TextView habitDescription;

            public DailyListHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.todo_list_card_view);
                isDone = itemView.findViewById(R.id.todo_list_element_is_done);
                habitTitle = itemView.findViewById(R.id.todo_list_element_title);
                habitDescription = itemView.findViewById(R.id.todo_list_element_description);
            }
        }
    }

    public static void removeHabit(String name) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("dailies");
        dbRef.child(name).removeValue();
    }
}