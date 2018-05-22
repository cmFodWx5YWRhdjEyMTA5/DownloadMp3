package com.mp3downloader.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mp3downloader.R;
import com.mp3downloader.musicgo.DownloadFragment;
import com.mp3downloader.musicgo.RecommendFragment;

import java.util.ArrayList;
import java.util.Locale;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class HomePageAdapter extends FragmentPagerAdapter {

    private ArrayList<SupportFragment> mList = new ArrayList<>();

    private final String[] titles;

    public HomePageAdapter(Context context,  FragmentManager fm) {
        super(fm);
        mList.add(new RecommendFragment());
        mList.add(new DownloadFragment());

        titles = new String[]{
                context.getResources().getString(R.string.recommend),
                context.getResources().getString(R.string.download),
        };
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position]
                .toUpperCase(Locale.getDefault());
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
