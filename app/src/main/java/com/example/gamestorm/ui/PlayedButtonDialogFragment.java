package com.example.gamestorm.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gamestorm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlayedButtonDialogFragment extends DialogFragment {
    private boolean isPlayed = true;
    private boolean isPlaying;
    GameActivity activity;
    Bundle bundle;

    public PlayedButtonDialogFragment(GameActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        bundle = getArguments();
        String[] options = {getString(R.string.played), getString(R.string.playing)};
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(requireContext());
        alertDialog.setTitle(bundle.getString("gameName"));
        alertDialog.setPositiveButton(getString(R.string.confirm_text), (dialog, which) -> {
            GameActivity gameActivity = activity;
            gameActivity.getWantedButton().setVisibility(View.GONE);
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference docRef = firebaseFirestore.collection("User").document(bundle.getString("idUser"));
            if (isPlaying) {
                gameActivity.getPlayedButton().setText(R.string.alreadyPlaying);
                docRef.update("playingGames", FieldValue.arrayUnion(bundle.get("idGame")));
            }
            if (isPlayed) {
                gameActivity.getPlayedButton().setText(R.string.alreadyPlayed);
                docRef.update("playedGames", FieldValue.arrayUnion(bundle.get("idGame")));
            }
            dismiss();
        });
        alertDialog.setNegativeButton(getString(R.string.cancel_text), (dialog, which) -> dismiss());
        alertDialog.setSingleChoiceItems(options, 0, (dialog, which) -> {
            switch (which) {
                case 0:
                    isPlaying = false;
                    isPlayed = true;
                    break;
                case 1:
                    isPlaying = true;
                    isPlayed = false;
                    break;
            }
        });
        return alertDialog.create();
    }
}
