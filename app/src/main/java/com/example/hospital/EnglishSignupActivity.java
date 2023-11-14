package com.example.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnglishSignupActivity extends AppCompatActivity {

    TextView textView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_english_signup);
        textView = findViewById(R.id.englishlogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnglishSignupActivity.this, EnglishLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(v -> {
            final EditText Name = (EditText) findViewById(R.id.username);
            String name = Name.getText().toString().toLowerCase();
            final EditText Number = (EditText) findViewById(R.id.number);
            String number = Number.getText().toString().toLowerCase();
            final EditText Password = (EditText) findViewById(R.id.password);
            String password = Password.getText().toString().toLowerCase();
            final EditText Cpassword = (EditText) findViewById(R.id.cpassword);
            String cpassword = Cpassword.getText().toString().toLowerCase();
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference("user");
            reference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Name.setError("Name Already Exists");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if ((TextUtils.isEmpty(name))) {
                Name.setError("Field Cnnot be Empty");
            } else if (!name.matches("[a-zA-Z ]+")) {
                Name.setError("Name must be letters not numbers");
            } else if ((TextUtils.isEmpty(number))) {
                Number.setError("Field Cnnot be Empty");
            } else if ((TextUtils.isEmpty(password))) {
                Password.setError("Field Cnnot be Empty");
            } else if ((TextUtils.isEmpty(cpassword))) {
                Cpassword.setError("Field Cnnot be Empty");
            } else if (!cpassword.matches(password)) {
                Cpassword.setError("Cpassword not mactched with password");
            } else {
                User user = new User(name, number, password, cpassword);
                reference.child(name).setValue(user);
                Toast.makeText(EnglishSignupActivity.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EnglishSignupActivity.this, EnglishLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
        finish();
    }
}