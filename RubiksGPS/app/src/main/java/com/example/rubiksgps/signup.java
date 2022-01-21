package com.example.rubiksgps;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rubiksgps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class signup extends AppCompatActivity implements  View.OnClickListener{

    ProgressBar mProgressBar;
    EditText editTextEmail, editTextPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextEmail = findViewById(R.id.editTextUser);
        editTextPass = findViewById(R.id.editTextPass);
        mProgressBar = findViewById(R.id.progressBar);
        findViewById(R.id.signupButton).setOnClickListener(this);
        findViewById(R.id.textView2).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

    }
    private void registerUser(){
        String username = editTextEmail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        if (username.isEmpty()){
            editTextEmail.setError("Introduce un correo");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextPass.setError("Introduce un password");
            editTextPass.requestFocus();
            return;
        }
        if (password.length()<6){
            editTextPass.setError("Mas de 6 caracteres");
            editTextPass.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            editTextEmail.setError("Introduce un correo valido");
            editTextEmail.requestFocus();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    finish();
                    Toast.makeText(getApplicationContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    //Clear all the open startActivities, so dont come back to login form
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(), "Estos datos ya pertenecen a otra cuenta", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signupButton:
                registerUser();
                break;
            case R.id.textView2:
                finish();
                startActivity(new Intent(this, login.class));
                break;
        }
    }
}
