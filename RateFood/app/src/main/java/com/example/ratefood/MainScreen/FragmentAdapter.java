package com.example.ratefood.MainScreen;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Robert on 2017-12-06.
 */

class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TrendingFragment();
            case 1:
                return new HotFragment();
            case 2:
                return new NewFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            //
            //Your tab titles
            //
            case 0:return "Trending";
            case 1:return "Hot";
            case 2: return "New";
            default:return null;
        }
    }
}