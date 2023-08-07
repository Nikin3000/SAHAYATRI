package com.example.proto3.CustomerActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class CustomerLoginActivity extends AppCompatActivity {
    Button login;
    TextView signup;
    EditText mail,password;
    FirebaseAuth auth;
    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        login=findViewById(R.id.riderlogin);
        signup=findViewById(R.id.riderClickhere);
        mail=findViewById(R.id.riderloginmail);
        password=findViewById(R.id.riderloginpassword);
        cb=findViewById(R.id.checkbox);
        auth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator(mail);
                if (valid()){
                    auth.signInWithEmailAndPassword(mail.getText().toString(),
                            password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent i =new Intent(CustomerLoginActivity.this, CustomersMapActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CustomerLoginActivity.this,"Please verify again",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
            private void emailValidator(EditText mail) {
                String emailToText = mail.getText().toString();
                if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
                } else {

                    Toast.makeText(CustomerLoginActivity.this,"Enter valid email address",Toast.LENGTH_SHORT).show();
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
                Intent i=new Intent(CustomerLoginActivity.this, CustomerSignupActivity.class);
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
}