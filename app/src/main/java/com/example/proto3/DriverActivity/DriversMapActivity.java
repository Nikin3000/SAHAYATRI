package com.example.proto3.DriverActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Looper;

import android.os.Handler;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

// from others github

/*import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;*/
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.proto3.CustomerActivity.CustomersMapActivity;
import com.example.proto3.R;
import com.example.proto3.Services.SendMessageTask;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DriversMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location lastLocation;
    LocationRequest locationRequest;


    private FusedLocationProviderClient mFusedLocationClient;


    private float rideDistance;
    private String customerID = "", destination;
    private LatLng destinationLatLng, pickupLatLng;

    private Switch mWorkingSwitch;

    private LinearLayout mCustomerInfo;

    private ImageView mCustomerProfileImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;
    private Button mRideStatus, mRideCompleted;

    private MediaPlayer mediaPlayer;
    private int status = 0;


    //for waiting time
    Handler handler = new Handler();
    int delayDuration = 2000;

    private DatabaseReference mDriverDatabase;
    private String userID;

//    private final ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // FCM SDK (and your app) can post notifications.
//                } else {
//                    // TODO: Inform user that that your app will not show notifications.
//                }
//            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        System.out.println("User UID: " + userID);
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Check if the "name" field exists
                    if (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("phone")) {
                        // The "name" field exists
                        mDriverDatabase.removeEventListener(this);
                        // Use the name
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill the personal information first.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext()
                                , DrivAccActivity.class));
                        overridePendingTransition(0, 0);
                        mDriverDatabase.removeEventListener(this);
                    }

                } else {
                    System.out.println("The user doesn't exits.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });



        polylines = new ArrayList<>();



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext()
                                , DrivAccActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_menu:
                        startActivity(new Intent(getApplicationContext()
                                , DrivMenuActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext()
                                , DriversMapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(this);

        //customer info display
        mCustomerInfo = findViewById(R.id.customerInfo);
        mCustomerName = findViewById(R.id.cusName);
        mCustomerPhone = findViewById(R.id.cusPhone);
        mCustomerDestination = findViewById(R.id.cusDestination);
        mCustomerProfileImage = findViewById(R.id.customerProfileImage);

        mCustomerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the phone number from the TextView
                String phoneNumber = mCustomerPhone.getText().toString();

                // Create an Intent to initiate a phone call
                Intent callIntent = new Intent(Intent.ACTION_CALL);

                // Set the phone number in the Intent's data
                callIntent.setData(Uri.parse("tel:" + phoneNumber));

                // Check for CALL_PHONE permission before initiating the call
                if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    // Request CALL_PHONE permission if not granted
                    requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                }
            }
        });

        mWorkingSwitch = findViewById(R.id.status_driver);
        mRideCompleted = findViewById(R.id.rideCompleted);
//        mRideStatus = (Button) findViewById(R.id.rideStatus);
//        mRideStatus.setOnClickListener(v -> {
//            switch(status){
//                case 1:
////                        status=2;
//                    erasePolylines();
//                    if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
//                        getRouteToMarker(destinationLatLng);
//                    }
//                    mRideStatus.setText("drive completed");
//
//                    break;
//                case 2:
//                    //recordRide();
//                    endRide();
//                    break;
//            }
//        });

        mWorkingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                connectDriver();
            } else {
                disconnectDriver();
            }
        });

        mRideCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDriverDatabase.child("customerRequest").removeValue();
                mRideCompleted.setVisibility(View.GONE);
                mCustomerInfo.setVisibility(View.GONE);

                customerID = "";
//                FirebaseDatabase.getInstance().getReference("driversWorking").child(userID).removeValue();



            }
        });



    }

    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    customerID = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
//                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationRequest = new LocationRequest();
        locationRequest.setInterval(8000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LatLng nepalLatLng = new LatLng(27.7172, 85.3240);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nepalLatLng));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    if (!customerID.equals("") && lastLocation != null && location != null) {
                        rideDistance += lastLocation.distanceTo(location) / 1000;
                    }
                    lastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(40));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (customerID) {
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId);
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(DriversMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(DriversMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private void connectDriver() {
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
//        mRideStatus.setText("Waiting for requests.");


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAssignedCustomer();
            }
        }, delayDuration);


    }


    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerID).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerID.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



//    private void getAssignedCustomerDestination(){
//        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
//        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
////                    if(map.get("destination")!=null){
////                        destination = map.get("destination").toString();
////                        mCustomerDestination.setText("Destination: " + destination);
////                    }
////                    else{
////                        mCustomerDestination.setText("Destination: --");
////                    }
//
////                    Double destinationLat = 0.0;
////                    Double destinationLng = 0.0;
////                    if(map.get("destinationLat") != null){
////                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
////                    }
////                    if(map.get("destinationLng") != null){
////                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
////                        destinationLatLng = new LatLng(destinationLat, destinationLng);
////                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }


    //getting assigned Customer Request

    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }

                    if(map.get("latestDestination")!=null){
                        mCustomerDestination.setText(map.get("latestDestination").toString());
                    }

                    if(dataSnapshot.child("profileURL").getValue()!=null){
                        String profileURL =map.get("profileURL").toString();
                        Glide.with(DriversMapActivity.this)
                                .load(profileURL)
                                .placeholder(R.drawable.placeholderimage) // Placeholder image while loading
                                .error(R.drawable.errorimage) // Error image if loading fails
                                .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
                                .into(mCustomerProfileImage);
                    }

                    mediaPlayer = MediaPlayer.create(DriversMapActivity.this, R.raw.notify);
                    mediaPlayer.start();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mRideCompleted.setVisibility(View.VISIBLE);
    }


    private void getRouteToMarker(LatLng pickupLatLng) {

        String serverKey = getResources().getString(R.string.google_maps_key);
        if (destination != null && lastLocation != null) {
            GoogleDirection.withServerKey(serverKey)
                    .from(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                    .to(pickupLatLng)
                    .transportMode(TransportMode.DRIVING)
                    .execute((DirectionCallback) this);
        }

        //        if (pickupLatLng != null && lastLocation != null){
//            Routing routing = new Routing.Builder()
//                    .travelMode(AbstractRouting.TravelMode.DRIVING)
//                    .withListener(this)
//                    .alternativeRoutes(false)
//                    .waypoints(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), pickupLatLng)
//                    .build();
//            routing.execute();
//        }
    }


    private void disconnectDriver() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }

    private List<Polyline> polylines;
    private void endRide(){
//        mRideStatus.setText("Ride End");
        erasePolylines();

//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
//        driverRef.removeValue();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.removeLocation(customerID);
//        customerID="";
//        rideDistance = 0;
//
//        if(pickupMarker != null){
//            pickupMarker.remove();
//        }
//        if (assignedCustomerPickupLocationRefListener != null){
//            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
//        }
//

//        mCustomerInfo.setVisibility(View.GONE);
//        mCustomerName.setText("");
//        mCustomerPhone.setText("");
//        mCustomerDestination.setText("Destination: --");
//        mCustomerProfileImage.setImageResource(R.mipmap.ic_pickup);
    }



    protected void onStop()
    {
        super.onStop();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release MediaPlayer resources
        }
    }



}
