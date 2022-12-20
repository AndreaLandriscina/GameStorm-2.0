package com.example.gamestorm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.R;
import com.example.gamestorm.databinding.ActivityLoginBinding;
import com.example.gamestorm.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    LayoutInflater inflater;
    Button logoutButton;
    ConstraintLayout logoutLayout;
    TextView usernameText;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;
    Bundle extras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater=LayoutInflater.from(getContext());
        tabLayout = requireView().findViewById(R.id.tab_layout);
        viewPager2 = requireView().findViewById(R.id.view_pager);
        usernameText = requireView().findViewById(R.id.username_text);
        myViewPagerAdapter = new MyViewPagerAdapter((FragmentActivity) getContext());
        viewPager2.setAdapter(myViewPagerAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        logoutButton = requireView().findViewById(R.id.logoutButton);
        logoutLayout = requireView().findViewById(R.id.logout_layout);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(),gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null){
            usernameText.setText(account.getDisplayName());
        }

        if (firebaseAuth.getCurrentUser()!= null) {
            FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
            usernameText.setText(user.getDisplayName());
        }

        if (firebaseAuth.getCurrentUser()==null && account == null) {
            logoutLayout.setVisibility(View.GONE);
        }else{
                logoutLayout.setVisibility(View.VISIBLE);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        progressDialog = new ProgressDialog(getContext());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.cancel();
                //LOGOUT CON MAIL E PASSWORD
                if (firebaseAuth.getCurrentUser()!=null) {
                    try {
                        firebaseAuth.signOut();
                        progressDialog.cancel();
                        Toast.makeText(getContext(), R.string.logout_successfully, Toast.LENGTH_SHORT).show();
                        logoutLayout.setVisibility(View.GONE);
                        startActivity(new Intent(getContext(), MainActivity.class));
                        requireActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.cancel();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                //LOGOUT CON GOOGLE
                if (account != null){
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            requireActivity().finish();
                            progressDialog.cancel();
                            Toast.makeText(getContext(), R.string.logout_successfully, Toast.LENGTH_SHORT).show();
                            logoutLayout.setVisibility(View.GONE);
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}