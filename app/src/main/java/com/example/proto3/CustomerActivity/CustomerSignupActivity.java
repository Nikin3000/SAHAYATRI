package com.example.proto3.CustomerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class CustomerSignupActivity extends AppCompatActivity {
    Button register;
    EditText vemail, password;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);
        auth = FirebaseAuth.getInstance();
        register = findViewById(R.id.customersignup);
        vemail = findViewById(R.id.signupcustomermail);
        password = findViewById(R.id.signupcustomerpassword);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator(vemail);
                if (valid()) {
                    auth.createUserWithEmailAndPassword(vemail.getText().toString(),
                            password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(CustomerSignupActivity.this, "User registration successfull", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(CustomerSignupActivity.this, CustomerLoginActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CustomerSignupActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            private void emailValidator(TextView vemail) {
                String emailToText = vemail.getText().toString();
                if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
//                    Toast.makeText(RegisterActivity.this,"Email Verified",Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(CustomerSignupActivity.this,"Enter valid email address",Toast.LENGTH_SHORT).show();
                }
            }
            private boolean valid() {
                boolean validation = true;
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Password cannot be empty");
                    validation = false;
                }
                if (TextUtils.isEmpty(vemail.getText().toString())) {
                    vemail.setError("Email cannot be empty");
                    validation = false;
                }
                return validation;
            }
        });

    }
}
