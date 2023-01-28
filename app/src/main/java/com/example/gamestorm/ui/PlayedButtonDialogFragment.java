package com.example.gamestorm.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gamestorm.R;

public class PlayedButtonDialogFragment extends Dialog implements View.OnClickListener {

    private View dialogView;
    private String title;
    private static boolean isPlayed;
    private static boolean isPlaying;
    public static boolean isSavePressed;
    GameActivity activity;
    Button save, cancel;
    public PlayedButtonDialogFragment(GameActivity activity, String title) {
        super(activity);
        this.activity = activity;
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        TextView titleView = findViewById(R.id.title);
        titleView.setText(title);
        save = findViewById(R.id.saveButton);
        cancel = findViewById(R.id.cancelButton);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.playingButton) {
                isPlaying = true;
                isPlayed = false;
            } else if (checkedId == R.id.playedButton) {
                isPlaying = false;
                isPlayed = true;
            }
        });
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                Log.i("ok","ok");
                GameActivity gameActivity = activity;
                gameActivity.getWantedButton().setVisibility(View.GONE);
                isSavePressed = true;
                break;
            case R.id.cancelButton:
                dismiss();
                break;
        }
        dismiss();
    }
}
