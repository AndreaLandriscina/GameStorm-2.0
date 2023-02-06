package com.example.gamestorm.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
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
import com.example.gamestorm.adapter.MyViewPagerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    LayoutInflater inflater;
    ConstraintLayout logoutLayout;
    TextView usernameText;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    MenuItem logout_option;
    TextView nDesiredGames;
    TextView nPlayedGames;
    String loggedUserID;

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;
    FirebaseFirestore firebaseFirestore;

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
        myViewPagerAdapter = new MyViewPagerAdapter((FragmentActivity) requireContext());
        viewPager2.setAdapter(myViewPagerAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        logoutLayout = requireView().findViewById(R.id.logout_layout);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1091326442567-dbkvi0h9877eego2ou819bepnb05h65g.apps.googleusercontent.com").requestEmail().build();
        gsc = GoogleSignIn.getClient(requireContext(),gso);
        nDesiredGames = requireView().findViewById(R.id.nDesiredGames);
        nPlayedGames = requireView().findViewById(R.id.nPlayedGames);
        firebaseFirestore=FirebaseFirestore.getInstance();

        setHasOptionsMenu(true);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account != null){
            usernameText.setText("Bentornato, " + account.getDisplayName());
        }

        if (firebaseAuth.getCurrentUser()!= null) {
            FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
            usernameText.setText("Bentornato, " + user.getDisplayName());
        }

        if (firebaseAuth.getCurrentUser()!=null){
            loggedUserID=firebaseAuth.getCurrentUser().getUid();
        } else if(account!=null){
            loggedUserID= account.getId();
        }

        if (firebaseAuth.getCurrentUser()==null && account == null) {
            logoutLayout.setVisibility(View.GONE);
        }else{
            logoutLayout.setVisibility(View.VISIBLE);
            DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<Integer> desiredGames = (ArrayList<Integer>) document.get("desiredGames");
                        nDesiredGames.setText("Giochi desiderati: " + desiredGames.size());
                        ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                        nPlayedGames.setText("Giochi giocati: " + playedGames.size());
                    }
                }
            });
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
        logout_option.setVisible(firebaseAuth.getCurrentUser() != null || account != null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_option:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
                progressDialog.setTitle(getString(R.string.logout_in_progress));
                progressDialog.show();

                //LOGOUT CON MAIL E PASSWORD
                if (firebaseAuth.getCurrentUser()!=null) {
                    try {
                        firebaseAuth.signOut();
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
        if (firebaseAuth.getCurrentUser()==null && account == null) {
            logoutLayout.setVisibility(View.GONE);
        }else{
            logoutLayout.setVisibility(View.VISIBLE);
            DocumentReference docRef = firebaseFirestore.collection("User").document(loggedUserID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<Integer> desiredGames = (ArrayList<Integer>) document.get("desiredGames");
                        nDesiredGames.setText("Giochi desiderati: " + desiredGames.size());
                        ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                        nPlayedGames.setText("Giochi giocati: " + playedGames.size());
                    }
                }
            });
        }
    }
}