package com.example.gamestorm.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gamestorm.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenshotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenshotFragment extends Fragment {
    private ArrayList<String> imageUrls;
    private String currentImage;
    private int position;

    public ScreenshotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @return A new instance of fragment ScreenshotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreenshotFragment newInstance(String param1) {
        return new ScreenshotFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            currentImage = getArguments().getString("currentImage");
            imageUrls = getArguments().getStringArrayList("imageUrl");
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView imageView = requireView().findViewById(R.id.screenshotFragmentView);
        Button leftButton = requireView().findViewById(R.id.leftButton);
        Button rightButton = requireView().findViewById(R.id.rightButton);

        AtomicReference<String> newUrl = new AtomicReference<>(currentImage.replace("thumb", "screenshot_med"));

        Picasso.get().load(newUrl.get()).into(imageView);

        leftButton.setOnClickListener(v -> {
            Log.i("w", "works");
            //left limit
            if (position > 0) {
                position--;
                newUrl.set(imageUrls.get(position).replace("thumb", "screenshot_med"));
                Picasso.get().load(newUrl.get()).into(imageView);
            }
        });
        rightButton.setOnClickListener(v -> {
            //right limit
            if (position < imageUrls.size() - 1){
                position++;
                newUrl.set(imageUrls.get(position).replace("thumb", "screenshot_med"));
                Picasso.get().load(newUrl.get()).into(imageView);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screenshot, container, false);
    }
}