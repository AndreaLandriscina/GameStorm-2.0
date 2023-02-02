package com.example.gamestorm.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gamestorm.R;
import com.example.gamestorm.adapter.SliderAdapter;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenshotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenshotFragment extends Fragment {

    private SliderView sliderView;
    private ArrayList<String> list;
    private int position;
    public ScreenshotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScreenshotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreenshotFragment newInstance(String param1, String param2) {
        ScreenshotFragment fragment = new ScreenshotFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = getArguments().getStringArrayList("imageUrl");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screenshot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sliderView = requireView().findViewById(R.id.image_slider);
        SliderAdapter sliderAdapter = new SliderAdapter(list);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setCurrentPagePosition(position);
    }
}