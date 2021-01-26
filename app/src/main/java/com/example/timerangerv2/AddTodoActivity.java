package com.example.timerangerv2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTodoActivity extends AppCompatActivity {
    private DatabaseReference DATABASE = FirebaseDatabase.getInstance()
            .getReference("todos");

    private EditText addTitle;
    private EditText addDescription;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        DatabaseReference todoRef = DATABASE.child("3");
        addTitle = findViewById(R.id.add_todo_title);
        addDescription = findViewById(R.id.add_todo_description);

        saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(view -> {
            String title = addTitle.getText().toString();
            String description = addDescription.getText().toString();

            todoRef.child("title").setValue(title);
            todoRef.child("description").setValue(description);
            finish();
        });
    }
}