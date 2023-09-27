package com.example.proto3.CustomerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.R;
import com.example.proto3.WelcomeActivity;
import com.firebase.geofire.GeoFire;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class CusAccActivity extends AppCompatActivity {

    private boolean islogout;
    private Button mLogout, mEdit;
    private TextView Name,Address,Email,Phone;
    private ImageView Profile;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private static final String USER = "user";
    private DatabaseReference mCusDatabase;

    private String userID,profileURL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_acc);

        Name = findViewById(R.id.profile_text);
        Address = findViewById(R.id.profile_address);
        Email = findViewById(R.id.profile_email);
        Phone = findViewById(R.id.profile_phone);
        Profile = findViewById(R.id.profileImageView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);


        mCusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);

                Name.setText(name);
                Address.setText(address);
                Email.setText(email);
                Phone.setText(phone);
                profileURL = snapshot.child("profileURL").getValue(String.class);
                Glide.with(CusAccActivity.this)
                        .load(profileURL)
                        .placeholder(R.drawable.placeholderimage) // Placeholder image while loading
                        .error(R.drawable.errorimage) // Error image if loading fails
                        .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
                        .into(Profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
        mEdit = (Button) findViewById(R.id.profile_edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startPageIntent = new Intent(CusAccActivity.this , CusSetProfileActivity.class);
                startActivity(startPageIntent);
                finish();

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

    public void LogOutUser()
    {
        FirebaseAuth.getInstance().signOut();
        Intent startPageIntent = new Intent(CusAccActivity.this , WelcomeActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

}
