package com.example.proto3.DriverActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.proto3.DriverActivity.DriversMapActivity;
import com.example.proto3.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.DatePickerDialog;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import java.util.HashMap;
import java.util.Map;

public class DriverSetProfileActivity extends AppCompatActivity {

    Button save, cancel, image;
    EditText nameC, addressC, birth, email, phone, taxi;

    RadioGroup genderGroup;

    FirebaseAuth auth;

    Calendar calendar;

    String selectedGender = "";

    Boolean canUploaded = true;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private ImageView mImageView;


    private DatabaseReference mDriverDatabase;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setprofile);

        auth = FirebaseAuth.getInstance();
        save = findViewById(R.id.saveButton);
        cancel = findViewById(R.id.backButton);
        nameC = findViewById(R.id.fullNameEditText);
        addressC = findViewById(R.id.permanentAddress);
        genderGroup = findViewById(R.id.genderRadioGroup);
        birth = findViewById(R.id.dateOfBirthEditText);
        email = findViewById(R.id.emailviewtext);
        phone = findViewById(R.id.mobileviewtext);
        taxi = findViewById(R.id.taxinumber);
        image = findViewById(R.id.importImageButton);
        mImageView = findViewById(R.id.profileImageView);

        calendar = Calendar.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        userID = auth.getUid();


        checkDataUser();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameDriver = nameC.getText().toString();
                String addressDriver = addressC.getText().toString();

                String dateOfBirth = birth.getText().toString();

                String taxiNum = taxi.getText().toString();



                int selectedRadioButtonId = genderGroup.getCheckedRadioButtonId();

                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

                    selectedGender = selectedRadioButton.getText().toString();

                } else {
                    Toast.makeText(DriverSetProfileActivity.this,"Please select the gender.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateOfBirth.isEmpty()) {
                    Toast.makeText(DriverSetProfileActivity.this, "Please select the date of birth.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (taxiNum.isEmpty()){
                    Toast.makeText(DriverSetProfileActivity.this, "Please enter the taxi number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mImageUri!=null){
                    System.out.println("Uploading");
                    uploadFile();
                    if (!canUploaded)
                        return;
                }
                else {
                    Toast.makeText(DriverSetProfileActivity.this, "Please select the profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDriverDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("Updating driver database");

                        if (nameDriver.length()!=0 && addressDriver.length() != 0) {
                            Map<String, Object> userInfo = new HashMap<String, Object>();
                            userInfo.put("name", nameDriver);
                            userInfo.put("address", addressDriver);
                            userInfo.put("gender", selectedGender);
                            userInfo.put("dateOfBirth", dateOfBirth);
                            userInfo.put("taxiNumber",taxiNum);
                            mDriverDatabase.updateChildren(userInfo);
                            Toast.makeText(DriverSetProfileActivity.this,"Profile information saved successfully",Toast.LENGTH_SHORT).show();
                            mDriverDatabase.removeEventListener(this);
                            startActivity(new Intent(DriverSetProfileActivity.this, DriversMapActivity.class));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverSetProfileActivity.this,"You can not edit your phone number", Toast.LENGTH_SHORT).show();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DriverSetProfileActivity.this,"You can not edit your email", Toast.LENGTH_SHORT).show();

            }
        });

    }



    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            Calendar minBirthYear = Calendar.getInstance();
            minBirthYear.add(Calendar.YEAR, -18);

            if (calendar.after(minBirthYear)) {
                // If the selected birthdate is not valid, show an error message
                birth.setError("You must be at least 18 years old.");
            } else {
                // If the selected birthdate is valid, format it and set it to the TextView
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDate = sdf.format(calendar.getTime());
                birth.setText(formattedDate);
            }

        }
    };

    public void showDatePickerDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                DriverSetProfileActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void checkDataUser(){

        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone2 = snapshot.child("phone").getValue(String.class);
                String email2 = snapshot.child("email").getValue(String.class);
                System.out.println("DriverInfo: "+ phone2 + "  "+ email2);
                phone.setText(phone2);
                email.setText(email2);
                if (snapshot.hasChild("name") && snapshot.hasChild("address")) {
                    String name = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    nameC.setText(name);
                    addressC.setText(address);
                }
                if (snapshot.hasChild("taxiNumber")){
                    taxi.setText(snapshot.child("taxiNumber").getValue(String.class));
                }
                if (snapshot.hasChild("dateOfBirth")){
                    birth.setText(snapshot.child("dateOfBirth").getValue(String.class));
                }
                if (snapshot.hasChild("profileURL")){
                    mImageUri = Uri.parse(snapshot.child("profileURL").getValue(String.class));

                    Glide.with(DriverSetProfileActivity.this)
                            .load(mImageUri)
                            .placeholder(R.drawable.placeholderimage) // Placeholder image while loading
                            .error(R.drawable.errorimage) // Error image if loading fails
                            .transition(DrawableTransitionOptions.withCrossFade()) // Smooth transition
                            .into(mImageView);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);

        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            try {

                InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                int fileSizeInBytes = imageStream.available();
                int fileSizeInKB = fileSizeInBytes / 1024; // Convert to KB
                int fileSizeInMB = fileSizeInKB / 1024; // Convert to MB

                if (fileSizeInMB > 1) {
                    // Image size exceeds 1MB limit, show an error message
                    Toast.makeText(this, "Image size exceeds 1MB limit", Toast.LENGTH_SHORT).show();
                    canUploaded = false;
                    return;
                }

                StorageReference mFileStore = mStorageRef.child("ProfileImage").child("Drivers/" + userID);
                mFileStore.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mFileStore.getDownloadUrl().addOnSuccessListener(uri -> {
                                    canUploaded = true;
                                    String imageUrl = uri.toString();
                                    // Now you have the image URL, you can store it in the Firebase Database
                                    mDriverDatabase.child("profileURL").setValue(imageUrl);
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("Exception: " + e);
                                // Handle unsuccessful upload
                            }
                        });
            }
            catch (Exception e){
                Log.e("Image upload: ", "Failed with "+e);
            }
        }
    }


}