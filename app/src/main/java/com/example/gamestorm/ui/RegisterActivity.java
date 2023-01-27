package com.example.gamestorm.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gamestorm.Model.UserModel;
import com.example.gamestorm.R;
import com.example.gamestorm.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.username.getText().toString();
                String email = binding.emailAddress.getText().toString().trim();
                String password = binding.password.getText().toString();

                progressDialog.show();
                if (isUsernameOk(name) && isEmailOk(email) && isPasswordOk(password)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    progressDialog.cancel();

                                    firebaseFirestore.collection("User")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .set(new UserModel(name, email, FirebaseAuth.getInstance().getUid()));
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build();

                                    user.updateProfile(profileUpdates);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            });
                }else{
                    Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),
                            R.string.check_data, Snackbar.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        });

        binding.goToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            binding.username.setError(getString(R.string.username_empty));
            return false;
        } else {
            binding.username.setError(null);
            return true;
        }
    }

    private boolean isEmailOk(String email) {
        if (email.isEmpty()) {
            binding.emailAddress.setError(getString(R.string.email_empty));
            return false;
        } else {
            binding.emailAddress.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            binding.password.setError(getString(R.string.password_empty));
            return false;
        } else {
            binding.password.setError(null);
            return true;
        }
    }


}