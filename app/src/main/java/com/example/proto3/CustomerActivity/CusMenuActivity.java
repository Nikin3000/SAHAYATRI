package com.example.proto3.CustomerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.proto3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CusMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext()
                                , CusAccActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_menu:
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

    }
}
