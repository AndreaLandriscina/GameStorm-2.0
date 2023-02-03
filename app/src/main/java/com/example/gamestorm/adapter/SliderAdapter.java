package com.example.gamestorm.adapter;

import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gamestorm.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {
    ArrayList<String> images;
    public SliderAdapter(ArrayList<String> images){
        this.images = images;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.screenshot_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.get().load(images.get(position)).into(viewHolder.imageViewBig);
    }

    @Override
    public int getCount() {
        return images.size();
    }
    public class Holder extends SliderViewAdapter.ViewHolder {
        ImageView imageViewBig;
        ImageView imageView;
        public Holder(View itemView) {
            super(itemView);
            imageViewBig = itemView.findViewById(R.id.screenshotBig);
            imageViewBig.setVisibility(View.VISIBLE);

            imageView = itemView.findViewById(R.id.screenshot);
            imageView.setVisibility(View.GONE);

        }
    }
}