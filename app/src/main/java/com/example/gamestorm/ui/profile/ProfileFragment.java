package com.example.gamestorm.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gamestorm.R;
import com.example.gamestorm.adapter.ProfilePagerAdapter;
import com.example.gamestorm.repository.user.IUserRepository;
import com.example.gamestorm.ui.MainActivity;
import com.example.gamestorm.ui.UserViewModel;
import com.example.gamestorm.ui.UserViewModelFactory;
import com.example.gamestorm.util.ServiceLocator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    ProgressDialog progressDialog;
    LayoutInflater inflater;
    ConstraintLayout logoutLayout;
    TextView usernameText;
    GoogleSignInClient gsc;
    MenuItem logout_option;
    TextView nDesiredGames;
    TextView nPlayedGames;

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfilePagerAdapter profilePagerAdapter;
    private UserViewModel userViewModel;

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
        nDesiredGames = requireView().findViewById(R.id.nDesiredGames);
        nPlayedGames = requireView().findViewById(R.id.nPlayedGames);

        setHasOptionsMenu(true);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account != null){
            usernameText.setText(getString(R.string.welcome_back) + " " + account.getDisplayName());
        }

        if (userViewModel.getLoggedUser() != null) {
            usernameText.setText(getString(R.string.welcome_back) + " " + userViewModel.getLoggedUser().getName());
            logoutLayout.setVisibility(View.VISIBLE);
        }
        if (userViewModel.getLoggedUser()==null && account == null) {
            logoutLayout.setVisibility(View.GONE);
        }else {
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
        switch (item.getItemId()){
            case R.id.logout_option:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
                progressDialog.setTitle(getString(R.string.logout_in_progress));
                //progressDialog.show();

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

                //LOGOUT CON GOOGLE
                if (account != null){
                        gsc.signOut().addOnCompleteListener(task -> {
                            requireActivity().finish();
                            progressDialog.cancel();
                            Toast.makeText(getContext(), R.string.logout_successfully, Toast.LENGTH_SHORT).show();
                            logoutLayout.setVisibility(View.GONE);
                            startActivity(new Intent(getContext(), MainActivity.class));
                        });

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (userViewModel.getLoggedUser() == null && account == null) {
            logoutLayout.setVisibility(View.GONE);
        }else{
            logoutLayout.setVisibility(View.VISIBLE);
        }
    }
}