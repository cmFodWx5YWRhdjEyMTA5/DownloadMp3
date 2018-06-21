package com.downloadermp3.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.downloadermp3.ui.HomeFragment;
import com.downloadermp3.ui.HotFragment;
import com.downloadermp3.ui.DownloadFragment;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private ArrayList<SupportFragment> mList = new ArrayList<>();

    public MainPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mList.add(new HomeFragment());
        mList.add(new HotFragment());
        mList.add(new DownloadFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}