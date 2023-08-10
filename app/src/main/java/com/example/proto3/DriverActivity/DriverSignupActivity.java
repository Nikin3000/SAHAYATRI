package com.example.proto3.DriverActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class DriverSignupActivity extends AppCompatActivity {
    Button register;
    EditText vemail, password, phone, editTextOTP;
    FirebaseAuth auth;

    String mVerification_id;
    String countrycode="+977";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);
        auth = FirebaseAuth.getInstance();
        register = findViewById(R.id.ridersignup);
        vemail = findViewById(R.id.signupridermail);
        password = findViewById(R.id.signupriderpassword);
        phone = findViewById(R.id.ridermobilenumber);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator(vemail);
                if (valid()) {
                    showOTPVerificationPopup();

                }

            }

            private boolean isValidPhone(TextView phone){
                String phoneNumber = phone.getText().toString();
                String strippedPhoneNumber = phoneNumber.replaceAll("[\\s\\-()]", "");
                // Check if the stripped phone number matches the nepali phone number pattern
                return strippedPhoneNumber.matches("^(98|97|96|95|94)\\d{8}$");
            }
            private void emailValidator(TextView vemail) {
                String emailToText = vemail.getText().toString();
                if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
//                    Toast.makeText(RegisterActivity.this,"Email Verified",Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(DriverSignupActivity.this,"Enter valid email address",Toast.LENGTH_SHORT).show();
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

                if (!isValidPhone(phone)) {
                    Toast.makeText(DriverSignupActivity.this,"Enter valid phone number",Toast.LENGTH_SHORT).show();
                    validation = false;
                }
                else{
                    String phoneNum = "+977" + phone.getText().toString();
                    requestOTP(phoneNum);
                }

                return validation;
            }

        });

    }

    public void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //When User doesnt get OTP and we force server to send the OTP code,Force Resending token is used
            //"s" contains the verification code id
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(DriverSignupActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
            }
//When OTP is not entered in the given time frame

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(DriverSignupActivity.this,"Cannot Create Account"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void showOTPVerificationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_driver_otp, null);
        editTextOTP = view.findViewById(R.id.editTextOTP);

        builder.setView(view)
                .setTitle("OTP Verification")
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userEnteredOTP = editTextOTP.getText().toString();
                        // Call the method to verify the OTP
                        verifyOTP(userEnteredOTP);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void verifyOTP(String userOTP) {
        if (!userOTP.isEmpty() && userOTP.length()==6){
            PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerification_id,userOTP);
            verifyAuth(credential);
        }
        else{
            Toast.makeText(DriverSignupActivity.this,"Wrong Verification Code",Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyAuth(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    auth.createUserWithEmailAndPassword(vemail.getText().toString(),
                            password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(DriverSignupActivity.this, "User registration successfull", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(DriverSignupActivity.this, RiderLoginActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DriverSignupActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(DriverSignupActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
