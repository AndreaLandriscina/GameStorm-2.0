package com.example.gamestorm.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gamestorm.ui.DesiredFragment;
import com.example.gamestorm.ui.PlayedFragment;
import com.example.gamestorm.ui.PlayingFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new DesiredFragment();
            case 1:
                return new PlayingFragment();
            case 2:
                return new PlayedFragment();
            default:
                return new DesiredFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
