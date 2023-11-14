package com.example.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TamilSignupActivity extends AppCompatActivity {

    TextView textView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamil_signup);

        textView=findViewById(R.id.tamillogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TamilSignupActivity.this, TamilLoginActivity.class);
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
                Name.setError("புலம் காலியாக இருக்கக்கூடாது");
            } else if (!name.matches("[a-zA-Z ]+")) {
                Name.setError("பெயர் எண்களாக இல்லாமல் எழுத்துக்களாக இருக்க வேண்டும்");
            } else if ((TextUtils.isEmpty(number))) {
                Number.setError("புலம் காலியாக இருக்கக்கூடாது");
            } else if ((TextUtils.isEmpty(password))) {
                Password.setError("புலம் காலியாக இருக்கக்கூடாது");
            } else if ((TextUtils.isEmpty(cpassword))) {
                Cpassword.setError("புலம் காலியாக இருக்கக்கூடாது");
            } else if (!cpassword.matches(password)) {
                Cpassword.setError("கடவுச்சொல்லுடன் குறுக்குச்சொல் இணைக்கப்படவில்லை");
            } else {
                User user = new User(name, number, password, cpassword);
                reference.child(name).setValue(user);
                Toast.makeText(TamilSignupActivity.this, "நீங்கள் வெற்றிகரமாக பதிவுசெய்துள்ளீர்கள்!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TamilSignupActivity.this, TamilLoginActivity.class);
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