package com.example.proto3.CustomerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proto3.R;
import com.example.proto3.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CusAccActivity extends AppCompatActivity {

    private boolean islogout;

    private Button mLogout, mSave;
    private TextView Name,Address,Email,Phone;
    private ImageView Image;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private static final String USER = "user";
    private DatabaseReference mCusDatabase;
    private String userID;
    String email;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_acc);

        Name = findViewById(R.id.profile_text);
        Address = findViewById(R.id.profile_address);
        Email = findViewById(R.id.profile_email);
        Phone = findViewById(R.id.profile_phone);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_account);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_account:
                        return true;
                    case R.id.nav_menu:
                        startActivity(new Intent(getApplicationContext()
                                , CusMenuActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext()
                                , CustomersMapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        //Save info
        mSave = (Button) findViewById(R.id.profile_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        //Customer logout activity
        mLogout = (Button) findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                islogout = true;
                LogOutUser();
            }
        });

    }
    private void saveUserInformation() {
        String name = Name.getText().toString();
        String phone = Phone.getText().toString();
        String address = Address.getText().toString();
        String email = Email.getText().toString();

        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("email", email);
        userInfo.put("address", address);
        mCusDatabase.updateChildren(userInfo);
    }


    public void LogOutUser()
    {
        Intent startPageIntent = new Intent(CusAccActivity.this , WelcomeActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

}
