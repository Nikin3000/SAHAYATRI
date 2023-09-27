package com.example.proto3.DriverActivity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proto3.CustomerActivity.CusMenuActivity;
import com.example.proto3.DriverActivity.DrivAccActivity;
import com.example.proto3.DriverActivity.DriversMapActivity;
import com.example.proto3.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DrivMenuActivity extends AppCompatActivity {
    TextView aboutus,contact,privacy,license,faq;
    LinearLayout Laboutus,Lcontact,LPrivacy,LLicense,LFaq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driv_menu);
        aboutus=findViewById(R.id.aboutus);
        Laboutus=findViewById(R.id.Laboutus);
        contact=findViewById(R.id.contact);
        Lcontact=findViewById(R.id.Lcontact);
        privacy=findViewById(R.id.privacy);
        LPrivacy=findViewById(R.id.Lprivacy);
        license=findViewById(R.id.license);
        LLicense=findViewById(R.id.LLicense);
        faq=findViewById(R.id.faq);
        LFaq=findViewById(R.id.LFAQ);

        Utils.enableScroll(aboutus);
        Utils.enableScroll(contact);
        Utils.enableScroll(privacy);
        Utils.enableScroll(license);
        Utils.enableScroll(faq);

        aboutus.setMovementMethod(new ScrollingMovementMethod());
        contact.setMovementMethod(new ScrollingMovementMethod());
        privacy.setMovementMethod(new ScrollingMovementMethod());
        license.setMovementMethod(new ScrollingMovementMethod());
        faq.setMovementMethod(new ScrollingMovementMethod());
        LFaq.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        LLicense.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        LPrivacy.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        Lcontact.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        Laboutus.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


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
    public void expandaboutus(View view) {
        int v=(aboutus.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition( Laboutus,new AutoTransition());
        aboutus.setVisibility(v);
    }

    public void expandcontact(View view) {
        int v=(contact.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition( Lcontact,new AutoTransition());
        contact.setVisibility(v);
    }

    public void expandprivacy(View view) {
        int v=(privacy.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition( LPrivacy,new AutoTransition());
        privacy.setVisibility(v);
    }

    public void expandlicense(View view) {
        int v=(license.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition( LLicense,new AutoTransition());
        license.setVisibility(v);
    }

    public void expandfaq(View view) {
        int v=(faq.getVisibility()==View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition( LFaq,new AutoTransition());
        faq.setVisibility(v);
    }
    public static class Utils {
        public static void enableScroll(View view) {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setMovementMethod(new ScrollingMovementMethod());
            }

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
        }
    }
}