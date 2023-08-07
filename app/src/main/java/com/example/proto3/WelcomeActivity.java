package com.example.proto3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.CustomerActivity.CustomerLoginActivity;
import com.example.proto3.DriverActivity.RiderLoginActivity;


public class WelcomeActivity extends AppCompatActivity {
    Button rider,customer;
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
