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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CusAccActivity extends AppCompatActivity {

    private boolean islogout;

    TextView profilePhone, profileEmail, profileUsername, profilePassword;
    Button editProfile,mLogout;
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

        profilePhone = findViewById(R.id.Phone);
        profileEmail = findViewById(R.id.mail);
        profileUsername = findViewById(R.id.Name);
        profilePassword = findViewById(R.id.Password);
        editProfile = findViewById(R.id.editprofile);
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

        showAllUsersData();
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUsersData();
            }
        });
    }
    public void showAllUsersData(){
        Intent intent = getIntent();
        String phoneUsers = intent.getStringExtra("phone");
        String emailUsers = intent.getStringExtra("email");
        String usernameUsers = intent.getStringExtra("username");
        String passwordUsers = intent.getStringExtra("password");

        profilePhone.setText(phoneUsers);
        profileEmail.setText(emailUsers);
        profileUsername.setText(usernameUsers);
        profilePassword.setText(passwordUsers);
    }
    public void passUsersData(){
        String usersUsername = profileUsername.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUsersDatabase = reference.orderByChild("username").equalTo(usersUsername);
        checkUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String phoneFromDB = snapshot.child(usersUsername).child("phone").getValue(String.class);
                    String emailFromDB = snapshot.child(usersUsername).child("email").getValue(String.class);
                    String usernameFromDB = snapshot.child(usersUsername).child("username").getValue(String.class);
                    String passwordFromDB = snapshot.child(usersUsername).child("password").getValue(String.class);
                    Intent intent = new Intent(CusAccActivity.this, CusEditProfile.class);
                    intent.putExtra("phone", phoneFromDB);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("username", usernameFromDB);
                    intent.putExtra("password", passwordFromDB);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
editProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i=new Intent(CusAccActivity.this, CusEditProfile.class);
        startActivity(i);
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

