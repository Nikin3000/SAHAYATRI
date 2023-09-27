package com.example.proto3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.CustomerActivity.CustomerLoginActivity;
import com.example.proto3.DriverActivity.DrivAccActivity;
import com.example.proto3.DriverActivity.DriversMapActivity;
import com.example.proto3.DriverActivity.RiderLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class WelcomeActivity extends AppCompatActivity {
    Button rider,customer;
    private String userID;
    private DatabaseReference mDriverDatabase;
    private DatabaseReference mCustomerDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        rider=findViewById(R.id.btnrider);
        customer=findViewById(R.id.btncustomer);



        rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(WelcomeActivity.this, RiderLoginActivity.class);
                startActivity(i);
            }
        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(WelcomeActivity.this, CustomerLoginActivity.class);
                startActivity(i);
            }
        });
    }
}
