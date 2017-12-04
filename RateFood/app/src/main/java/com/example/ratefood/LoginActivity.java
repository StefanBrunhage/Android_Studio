package com.example.ratefood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText EmailEditText, PasswordEditText;
    TextView CheckEmail;



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


    }

    private void userLogin(){
        String email = EmailEditText.getText().toString().trim();
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
                            finish();
                            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else{
                            CheckEmail.setText("Email is not verified (Click to verify)");
                            CheckEmail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(LoginActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
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




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(mAuth.getCurrentUser() != null && user.isEmailVerified()){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
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
