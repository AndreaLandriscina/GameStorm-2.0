package com.example.gamestorm.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.R;
import com.example.gamestorm.adapter.ProfilePagerAdapter;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.MainActivity;
import com.example.gamestorm.ui.viewModel.UserViewModel;
import com.example.gamestorm.ui.viewModel.UserViewModelFactory;
import com.example.gamestorm.util.Constants;
import com.example.gamestorm.util.ServiceLocator;
import com.example.gamestorm.util.SharedPreferencesUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    ProgressDialog progressDialog;
    LayoutInflater inflater;
    LinearLayout logoutLayout;
    LinearLayout loginLayout;
    TextView usernameText;
    MenuItem logout_option;
    Button loginButton;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfilePagerAdapter profilePagerAdapter;
    private UserViewModel userViewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflater=LayoutInflater.from(getContext());
        tabLayout = requireView().findViewById(R.id.tab_layout);
        viewPager2 = requireView().findViewById(R.id.view_pager);
        usernameText = requireView().findViewById(R.id.username_text);
        profilePagerAdapter = new ProfilePagerAdapter((FragmentActivity) requireContext());
        viewPager2.setAdapter(profilePagerAdapter);
        logoutLayout = requireView().findViewById(R.id.logout_layout);
        loginLayout = requireView().findViewById(R.id.loginLayout);
        loginButton = requireView().findViewById(R.id.loginButton);

        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        setHasOptionsMenu(true);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        ImageView photoView;
        photoView = requireView().findViewById(R.id.photoProfile);

        loginButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_loginActivity));

        if (userViewModel.getLoggedUser() != null) {
            loginLayout.setVisibility(View.GONE);
            logoutLayout.setVisibility(View.VISIBLE);
            String name = sharedPreferencesUtil.readStringData(Constants.SHARED_PREFERENCES_FILE_NAME, Constants.USERNAME);
            usernameText.setText(getString(R.string.welcome_back) + " " + name);
            if (userViewModel.getLoggedUser().getPhotoProfile() != null){
                Picasso.get().load(userViewModel.getLoggedUser().getPhotoProfile()).into(photoView);
            }
        } else  {
            logoutLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
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
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });

        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        logout_option = menu.findItem(R.id.logout_option);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        logout_option.setVisible(userViewModel.getLoggedUser() != null || account != null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_option) {

            //LOGOUT CON MAIL E PASSWORD
            if (userViewModel.getLoggedUser() != null) {
                try {
                    userViewModel.logout();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        if (userViewModel.getLoggedUser() == null) {
            logoutLayout.setVisibility(View.GONE);
        } else {
            //String name = sharedPreferencesUtil.readStringData(Constants.SHARED_PREFERENCES_FILE_NAME, Constants.USERNAME);
            //usernameText.setText(getString(R.string.welcome_back) + " " + name);
            logoutLayout.setVisibility(View.VISIBLE);
        }
    }
}