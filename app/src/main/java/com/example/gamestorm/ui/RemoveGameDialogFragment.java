package com.example.gamestorm.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gamestorm.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RemoveGameDialogFragment extends DialogFragment {
    GameActivity activity;
    Bundle bundle;

    public RemoveGameDialogFragment(GameActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bundle = getArguments();
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
        DocumentReference docRef = firebaseFirestore.collection("User").document(bundle.getString("idUser"));
        GameActivity gameActivity = activity;
        return new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                .setMessage(R.string.RemoveGame)
                .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.confirm_text, (dialog, which) -> {
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                ArrayList<Integer> playingGames = (ArrayList<Integer>) document.get("playingGames");
                                if (playingGames != null && playingGames.contains(new Long(bundle.getInt("idGame")))) {
                                    docRef.update("playingGames", FieldValue.arrayRemove(bundle.get("idGame")));
                                }
                                ArrayList<Integer> playedGames = (ArrayList<Integer>) document.get("playedGames");
                                if (playedGames != null && playedGames.contains(new Long(bundle.getInt("idGame")))) {
                                    docRef.update("playedGames", FieldValue.arrayRemove(bundle.get("idGame")));
                                }
                                ArrayList<Integer> desiredGames = (ArrayList<Integer>) document.get("desiredGames");
                                if (desiredGames != null && desiredGames.contains(new Long(bundle.getInt("idGame")))) {
                                    docRef.update("desiredGames", FieldValue.arrayRemove(bundle.get("idGame")));
                                    gameActivity.getPlayedButton().setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                    gameActivity.getWantedButton().setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }).create();
    }
}
