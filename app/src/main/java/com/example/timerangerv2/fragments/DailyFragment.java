package com.example.timerangerv2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.timerangerv2.AddTodoActivity;
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

public class DailyFragment extends Fragment {
    private DatabaseReference DATABASE = FirebaseDatabase.getInstance().getReference("dailies");

    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

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
        recyclerView = view.findViewById(R.id.daily_recycler_view_container);
        addButton = view.findViewById(R.id.add_daily_button);

        DATABASE.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Task> dailyList = new ArrayList<>();
                snapshot.getChildren().forEach(e -> {
                    dailyList.add(new Task(e.getKey(), e.child("title").getValue(String.class), "daily task"));
                });
                recyclerView.setAdapter(new DailyListAdapter(dailyList, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(recyclerView.findViewById(R.id.todo_recycler_view_container), "Daily task not added.", Snackbar.LENGTH_LONG).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DatabaseReference dailyRef = DATABASE.child("2");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyRef.child("title").setValue("fajne zadanie");
                dailyRef.child("description").setValue("fajny opis");
                Intent intent = new Intent(getActivity(), AddTodoActivity.class);
                startActivityForResult(intent, 1);
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

        @Override
        public void onBindViewHolder(@NonNull DailyListHolder holder, int position) {
            holder.isDone.setChecked(this.dailyList.get(position).isCompleted());
            holder.taskTitle.setText(this.dailyList.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return dailyList.size();
        }

        private class DailyListHolder extends RecyclerView.ViewHolder {
            private final CheckBox isDone;
            private final TextView taskTitle;

            public DailyListHolder(@NonNull View itemView) {
                super(itemView);
                isDone = itemView.findViewById(R.id.todo_list_element_is_done);
                taskTitle = itemView.findViewById(R.id.todo_list_element_title);
            }
        }
    }
}