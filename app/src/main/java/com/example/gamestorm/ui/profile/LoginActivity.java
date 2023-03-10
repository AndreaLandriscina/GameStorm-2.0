package com.example.gamestorm.ui.profile;

import static com.example.gamestorm.R.string.emailsent;
import static com.example.gamestorm.util.Constants.EMAIL_ADDRESS;
import static com.example.gamestorm.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.gamestorm.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.gamestorm.util.Constants.ID_TOKEN;
import static com.example.gamestorm.util.Constants.PASSWORD;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYED;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FIRST_LOADING_PLAYING;
import static com.example.gamestorm.util.Constants.SHARED_PREFERENCES_FIRST_LOADING_WANTED;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.databinding.ActivityLoginBinding;
import com.example.gamestorm.model.User;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.MainActivity;
import com.example.gamestorm.ui.UserViewModel;
import com.example.gamestorm.ui.UserViewModelFactory;
import com.example.gamestorm.util.DataEncryptionUtil;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        dataEncryptionUtil = new DataEncryptionUtil(getApplication());

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        googleResponse();

        //CLICK SU RESET PASSWORD
        resetPassword();

        //CLICK SU REGISTRATI
        TextView textView = findViewById(R.id.goToSignUpActivity);
        textView.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        googleLogin();

        login();
    }

    private void googleResponse() {
        ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d("TAG", "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(this, authenticationResult -> {
                            saveLoginData(authenticationResult.getEmail(), null, authenticationResult.getId());
                            userViewModel.setAuthenticationError(false);
                            retrieveUserInformation(authenticationResult);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        });
                    }
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveUserInformation(User user) {
        //progressIndicator.setVisibility(View.VISIBLE)
        Log.i("recuper","0");
        userViewModel.getUserWantedGamesMutableLiveData(user.getId()).observe(
                this, userFavoriteNewsRetrievalResult -> {
                    Log.i("recuper","1");
                    sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                            SHARED_PREFERENCES_FIRST_LOADING_WANTED, false);
                }
        );
        userViewModel.getUserPlayingGamesMutableLiveData(user.getId()).observe(
                this, userFavoriteNewsRetrievalResult -> {
                    Log.i("recuper","2");
                    sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                            SHARED_PREFERENCES_FIRST_LOADING_PLAYING, false);
                }
        );
        userViewModel.getUserPlayedGamesMutableLiveData(user.getId()).observe(
                this, userFavoriteNewsRetrievalResult -> {
                    Log.i("recuper","3");
                    sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                            SHARED_PREFERENCES_FIRST_LOADING_PLAYED, false);
                }
        );
    }

    private void resetPassword() {
        TextView resetPassword = findViewById(R.id.resetPassword);
        resetPassword.setOnClickListener(view -> {
            String email = Objects.requireNonNull(binding.emailAddress.getText()).toString();
            if (isEmailOk(email)) {
                progressDialog.setTitle(getString(R.string.sending_mail));
                progressDialog.show();
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(unused -> {
                            progressDialog.cancel();
                            Toast.makeText(LoginActivity.this, emailsent, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.cancel();
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Snackbar.make(LoginActivity.this.findViewById(android.R.id.content),
                        R.string.insert_mail_to_reset_password, Snackbar.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });
    }

    private void googleLogin() {
        SignInButton googleButton = findViewById(R.id.googleButton);
        googleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    Log.d("TAG", "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                    activityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("TAG", e.getLocalizedMessage());

                    Snackbar.make(findViewById(android.R.id.content),
                            "no google account",
                            Snackbar.LENGTH_SHORT).show();
                }));
    }

    private void login() {
        Button buttonLogin = findViewById(R.id.login);
        buttonLogin.setOnClickListener(v -> {
            TextInputEditText emailTextInput = findViewById(R.id.emailAddress);
            TextInputEditText passwordTextInput = findViewById(R.id.password);
            String email = "";
            String password = "";
            if (emailTextInput.getText() == null) {
                Toast.makeText(this, "Email can't be null", Toast.LENGTH_SHORT).show();
            } else {
                email = emailTextInput.getText().toString().trim();
            }
            if (passwordTextInput.getText() == null) {
                Toast.makeText(this, "Password can't be null", Toast.LENGTH_SHORT).show();
            } else {
                password = passwordTextInput.getText().toString().trim();
            }
            // Start login if email and password are ok
            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    //progressIndicator.setVisibility(View.VISIBLE);
                    String finalEmail = email;
                    String finalPassword = password;
                    userViewModel.getUserMutableLiveData(null, email, password, true).observe(
                            this, result -> {
                                if (result != null) {
                                    saveLoginData(finalEmail, finalPassword, result.getId());
                                    userViewModel.setAuthenticationError(false);
                                    retrieveUserInformation(result);
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    View view = this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                    Toast.makeText(this, "Email e/o password errati", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(null, email, password, true);
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginData(String email, String password, String idToken) {
        try {
            sharedPreferencesUtil = new SharedPreferencesUtil(getApplication());
            sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                    SHARED_PREFERENCES_FIRST_LOADING_WANTED, true);
            sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                    SHARED_PREFERENCES_FIRST_LOADING_PLAYING, true);
            sharedPreferencesUtil.writeBooleanData(SHARED_PREFERENCES_FILE_NAME,
                    SHARED_PREFERENCES_FIRST_LOADING_PLAYED, true);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);

            if (password != null) {
                dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                        email.concat(":").concat(password));
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailOk(String email) {
        TextInputEditText emailTextInput = findViewById(R.id.emailAddress);
        if (email.isEmpty()) {
            emailTextInput.setError(getString(R.string.email_empty));
            return false;
        } else {
            emailTextInput.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        TextInputEditText passwordTextInput = findViewById(R.id.password);
        if (password.isEmpty()) {
            passwordTextInput.setError(getString(R.string.email_empty));
            return false;
        } else {
            passwordTextInput.setError(null);
            return true;
        }
    }
}