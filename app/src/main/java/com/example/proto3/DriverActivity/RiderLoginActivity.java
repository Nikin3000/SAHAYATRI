package com.example.proto3.DriverActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proto3.R;
import com.example.proto3.CustomerActivity.CusSetProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderLoginActivity extends AppCompatActivity {
    Button login;
    TextView signup;
    EditText mail, password;
    FirebaseAuth auth;
    CheckBox cb;

    String userID;
    DatabaseReference mDriverDatabase;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        login = findViewById(R.id.riderlogin);
        signup = findViewById(R.id.riderClickhere);
        mail = findViewById(R.id.riderloginmail);
        password = findViewById(R.id.riderloginpassword);
        cb = findViewById(R.id.checkbox);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            progressDialog = new ProgressDialog(RiderLoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            checkUserInfo();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator(mail);
                if (valid()) {

                    progressDialog = new ProgressDialog(RiderLoginActivity.this);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    auth.signInWithEmailAndPassword(mail.getText().toString(),
                            password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            userID = auth.getCurrentUser().getUid();
                            System.out.println("User UID: " + userID);

                            checkUserInfo();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RiderLoginActivity.this, "Please verify again", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            private void emailValidator(EditText mail) {
                String emailToText = mail.getText().toString();
                if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
                } else {

                    Toast.makeText(RiderLoginActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean valid() {
                boolean validation = true;
                if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Password cannot be empty");
                    validation = false;
                }
                if (TextUtils.isEmpty(mail.getText().toString())) {
                    mail.setError("Email cannot be empty");
                    validation = false;
                }
                return validation;
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RiderLoginActivity.this, DriverSignupActivity.class);
                startActivity(i);
            }
        });
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    public void checkUserInfo() {
        userID = auth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the "name" field exists
                    if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("phone")) {
                        progressDialog.dismiss();
                        Intent i = new Intent(RiderLoginActivity.this, DriversMapActivity.class);
                        startActivity(i);


                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill the personal information first.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        Intent i = new Intent(RiderLoginActivity.this, DriverSetProfileActivity.class);
                        startActivity(i);
                    }
                    mDriverDatabase.removeEventListener(this);
                } else {
                    System.out.println("The user doesn't exits.");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });


    }
}