package com.example.proto3.DriverActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.DriverActivity.DrivAccActivity;
import com.example.proto3.DriverActivity.DriversMapActivity;
import com.example.proto3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DrivMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driv_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext()
                                , DrivAccActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_menu:
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext()
                                , DriversMapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}
