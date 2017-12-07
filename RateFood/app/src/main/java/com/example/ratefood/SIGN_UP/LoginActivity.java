package com.example.ratefood.SIGN_UP;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ratefood.MainScreen.MainActivity;
import com.example.ratefood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText EmailEditText, PasswordEditText;
    TextView CheckEmail;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        EmailEditText = (EditText) findViewById(R.id.EmailEditText);
        PasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
        CheckEmail = (TextView) findViewById(R.id.CheckEmailTextView);

        findViewById(R.id.LoginTxt).setOnClickListener(this);
        findViewById(R.id.LoginBtn).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference();


    }

    private void userLogin()
    {
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

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                        StartMainActivity();
                    }
                    else{
                        CheckEmail.setText("Email is not verified (Click to verify)");
                        CheckEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        CheckEmail.setText("Emailverifications sent (Click to resend)");
                                    }
                                });
                            }
                        });
                    }
                }else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void StartMainActivity(){
        finish();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
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
            case R.id.LoginTxt:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.LoginBtn:
                userLogin();
                break;
        }
    }
}
