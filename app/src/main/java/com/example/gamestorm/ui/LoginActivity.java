package com.example.gamestorm.ui;

import static com.example.gamestorm.R.string.check_data;
import static com.example.gamestorm.R.string.emailsent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gamestorm.model.UserModel;
import com.example.gamestorm.R;
import com.example.gamestorm.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    SignInButton googleButton;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        googleButton = findViewById(R.id.googleButton);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1091326442567-dbkvi0h9877eego2ou819bepnb05h65g.apps.googleusercontent.com").requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //LOGIN CON CREDENZIALI
        binding.login.setOnClickListener(view -> {
            String email = binding.emailAddress.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            progressDialog.setTitle(getString(R.string.login_in_progress));
            progressDialog.show();
            if (isEmailOk(email) && isPasswordOk(password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            progressDialog.cancel();
                            Toast.makeText(LoginActivity.this, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.cancel();
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }else{
                Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),
                        check_data, Snackbar.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });

        //CLICK SU RESET PASSWORD
        binding.resetPassword.setOnClickListener(view -> {
            String email=binding.emailAddress.getText().toString();
            if(isEmailOk(email)) {
                progressDialog.setTitle(getString(R.string.sending_mail));
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.cancel();
                                Toast.makeText(LoginActivity.this, emailsent, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),
                        R.string.insert_mail_to_reset_password, Snackbar.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });

        //CLICK SU REGISTRATI
        binding.goToSignUpActivity.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        //CLICK SU LOGIN CON GOOGLE
        binding.googleButton.setOnClickListener(v -> {
            progressDialog.setTitle(getString(R.string.login_in_progress));
            progressDialog.show();
            Intent i = gsc.getSignInIntent();
            startActivityForResult(i, 1234);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ACCESSO CON GOOGLE
        if (requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            Toast.makeText(LoginActivity.this, R.string.login_successfully, Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            firebaseFirestore=FirebaseFirestore.getInstance();
                            firebaseFirestore.collection("User")
                                    .document(FirebaseAuth.getInstance().getUid())
                                    .set(new UserModel(account.getDisplayName(), account.getEmail(), account.getId()));
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(account.getDisplayName()).build();

                            user.updateProfile(profileUpdates);
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}