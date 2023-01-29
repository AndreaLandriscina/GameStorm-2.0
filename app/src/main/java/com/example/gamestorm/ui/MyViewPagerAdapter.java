package com.example.gamestorm.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
