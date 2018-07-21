package com.freedownloader.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.freedownloader.MusicApp;
import com.freedownloader.R;
import com.freedownloader.bean.MusicArchiveBean;
import com.freedownloader.ui.HomeFragment;
import com.freedownloader.ui.HotFragment;
import com.freedownloader.ui.MusicArchiveFragment;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private ArrayList<SupportFragment> mList = new ArrayList<>();

    private String title[] = {MusicApp.sContext.getString(R.string.home_text),
            MusicApp.sContext.getString(R.string.hot_text), MusicApp.sContext.getString(R.string.new_song),
            MusicApp.sContext.getString(R.string.top_song), MusicApp.sContext.getString(R.string.pop_song)
    };

    public MainPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mList.add(new HomeFragment());
        mList.add(new HotFragment());
        mList.add(MusicArchiveFragment.newInstances(MusicArchiveBean.NEW_TYPE));
        mList.add(MusicArchiveFragment.newInstances(MusicArchiveBean.TOP_TYPE));
        mList.add(MusicArchiveFragment.newInstances(MusicArchiveBean.POP_TYPE));
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
