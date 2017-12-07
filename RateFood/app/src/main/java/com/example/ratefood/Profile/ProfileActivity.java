package com.example.ratefood.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ImageReader;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ratefood.R;
import com.example.ratefood.SIGN_UP.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 1;
    private ImageView mProfilePicture;
    private Button changeProfileBtn;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabase;
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabase = mFirebaseDatabase.getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mProfilePicture = (ImageView) findViewById(R.id.profilePicture);
        changeProfileBtn = (Button) findViewById(R.id.change_profileBtn);

        changeProfileBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent galleryIntent = new Intent();
                galleryIntent.setType("profile_image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), CHOOSE_IMAGE);



            }
        });


        findViewById(R.id.profilePicture).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mUserDatabase = mFirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                HashMap<String, String> userMap = new HashMap<String, String>();
                userMap.put("profile_image", "default");
                mUserDatabase.setValue(userMap);

            }
        });

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("Profile_image").getValue().toString();
                String email = dataSnapshot.child("Email").getValue().toString();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Uri imageURI = data.getData();

            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(ProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                String current_user_id = mCurrentUser.getUid();

                StorageReference filePath = mImageStorage.child("Profile_image").child(current_user_id + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url = task.getResult().getDownloadUrl().toString();

                            mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Success Uploading.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }






    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }






}
