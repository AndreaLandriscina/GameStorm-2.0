package com.example.gamestorm.ui.profile;


import static com.example.gamestorm.util.Constants.EMAIL_ADDRESS;
import static com.example.gamestorm.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static com.example.gamestorm.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static com.example.gamestorm.util.Constants.ID_TOKEN;
import static com.example.gamestorm.util.Constants.PASSWORD;
import static com.example.gamestorm.util.Constants.USERNAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.example.gamestorm.databinding.ActivityRegisterBinding;
import com.example.gamestorm.R;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.UserViewModel;
import com.example.gamestorm.ui.UserViewModelFactory;
import com.example.gamestorm.util.DataEncryptionUtil;
import com.example.gamestorm.util.ServiceLocator;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
        dataEncryptionUtil = new DataEncryptionUtil(getApplication());

        //progressDialog=new ProgressDialog(this);

        binding.signup.setOnClickListener(view -> {
            String name = Objects.requireNonNull(Objects.requireNonNull(binding.username.getText()).toString().trim());
            String email = Objects.requireNonNull(Objects.requireNonNull(binding.emailAddress.getText()).toString().trim());
            String password = Objects.requireNonNull(Objects.requireNonNull(binding.password.getText()).toString().trim());

            //progressDialog.show();
            if (isUsernameOk(name) && isEmailOk(email) && isPasswordOk(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(name, email, password, false).observe(
                            this, result -> {
                                    saveLoginData(name, email, password, result.getId());
                                    userViewModel.setAuthenticationError(false);
                                    finish();
                            });
                } else {
                    userViewModel.getUser(name, email, password, false);
                }
            }else{
                Snackbar.make(RegisterActivity.this.findViewById(android.R.id.content),
                        R.string.check_data, Snackbar.LENGTH_SHORT).show();
                //progressDialog.cancel();
            }
        });

        binding.goToLoginActivity.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
    private void saveLoginData(String username, String email, String password, String idToken) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, USERNAME, username);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);
            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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