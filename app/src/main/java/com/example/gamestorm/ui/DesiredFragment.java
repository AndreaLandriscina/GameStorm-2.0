package com.example.gamestorm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gamestorm.R;
import com.example.gamestorm.databinding.ActivityLoginBinding;
import com.example.gamestorm.databinding.FragmentDesiredBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;


public class DesiredFragment extends Fragment {

    FragmentDesiredBinding binding;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    Button loginButton;
    LayoutInflater inflater;
    ConstraintLayout function_not_available_layout;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_desired, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentDesiredBinding.inflate(getLayoutInflater());
        firebaseAuth=FirebaseAuth.getInstance();
        function_not_available_layout=requireView().findViewById(R.id.function_not_available_layout);
        loginButton=requireView().findViewById(R.id.loginButton);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(),gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if(firebaseAuth.getCurrentUser()==null && account == null){
            function_not_available_layout.setVisibility(View.VISIBLE);
        }else{
            function_not_available_layout.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), LoginActivity.class);
            requireActivity().startActivity(myIntent);
        });
    }
}