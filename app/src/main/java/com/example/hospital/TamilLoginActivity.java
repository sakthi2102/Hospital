package com.example.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TamilLoginActivity extends AppCompatActivity {

    TextView textView;
    EditText name;
    EditText password;

    private static String dbname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamil_login);
        textView=findViewById(R.id.tamilsignup);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TamilLoginActivity.this, TamilSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
        name=findViewById(R.id.username);
        password=findViewById(R.id.password);
        Button btn=findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkuser();

            }
        });
    }
    private void checkuser() {
        String Uname = name.getText().toString().toLowerCase().trim();
        String Password = password.getText().toString().toLowerCase().trim();
        if (TextUtils.isEmpty(Uname)) {
            name.setError("புலம் காலியாக இருக்கக்கூடாது");
        } else if (!Uname.matches("[a-zA-Z ]+")) {
            name.setError("பெயர் எண்களாக இல்லாமல் எழுத்துக்களாக இருக்க வேண்டும்");
        } else if (TextUtils.isEmpty(Password)) {
            password.setError("புலம் காலியாக இருக்கக்கூடாது");
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.orderByChild("name").equalTo(Uname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbpassword = snapshot.child(Uname).child("password").getValue(String.class);
                    assert dbpassword != null;
                    if (dbpassword.equals(Password)) {
                        dbname = snapshot.child(Uname).child("name").getValue(String.class);
                        Intent intent1 = new Intent(TamilLoginActivity.this, RouteActivity.class);
                        startActivity(intent1);
                        finish();
                    } else {
                        password.setError("தவறான கடவுச்சொல்");
                    }
                } else {
                    name.setError("பயனர் இல்லை");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
        finish();
    }
}