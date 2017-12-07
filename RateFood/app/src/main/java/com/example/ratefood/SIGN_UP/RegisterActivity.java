package com.example.ratefood.SIGN_UP;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ratefood.MainScreen.MainActivity;
import com.example.ratefood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText EmailEditText, PasswordEditText;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference UserImagesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.RegisterTxt).setOnClickListener(this);

        EmailEditText = (EditText) findViewById(R.id.EmailEditText);
        PasswordEditText = (EditText) findViewById(R.id.PasswordEditText);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();
        UserImagesDatabase = mFirebaseDatabase.getReference();


        findViewById(R.id.RegisterBtn).setOnClickListener(this);


    }

    private void registerUser(){
        final String email = EmailEditText.getText().toString().trim();
        String password = PasswordEditText.getText().toString().trim();

        if(email.isEmpty()){
            EmailEditText.setError("Email is required");
            EmailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EmailEditText.setError("Please enter a valid email");
            EmailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            PasswordEditText.setError("Password is required");
            PasswordEditText.requestFocus();
            return;
        }
        if(password.length() < 6){
            PasswordEditText.setError("Minimum length of password should be 6");
            PasswordEditText.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification();

                    mDatabase = mFirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("Email", email);
                    userMap.put("Profile_image", "profile_image"); //Makes this a placeholder for the profile picture (sets it to a simple string because they don't have a picture yet)




                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                UserImagesDatabase = mDatabase.child("Images");
                                HashMap<String, String> imagesMap = new HashMap<>();
                                imagesMap.put("Image", "PlaceHolder"); //Vet inte hur jag skall skapa Images mapp utan att ha en fil där
                                UserImagesDatabase.setValue(imagesMap);

                                Toast.makeText(RegisterActivity.this, "Stored...", Toast.LENGTH_SHORT).show();
                                StartMainActivity();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void StartMainActivity(){
        finish();
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(mAuth.getCurrentUser() != null && user.isEmailVerified()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.RegisterTxt:
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.RegisterBtn:
                registerUser();
                break;
        }
    }
}
