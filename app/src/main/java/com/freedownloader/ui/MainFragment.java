package com.freedownloader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.freedownloader.MusicApp;
import com.freedownloader.R;
import com.freedownloader.adapter.MainPageAdapter;
import com.freedownloader.router.Router;

import me.yokeyword.fragmentation.SupportFragment;
import q.rorbin.badgeview.Badge;

/**
 * Created by liyanju on 2018/5/7.
 */

public class MainFragment extends SupportFragment implements IHomeFragment, BottomNavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "HomeFragment";

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void tabLayoutBg(boolean isYoutube) {
        if (isYoutube) {
            mBNavigation.setBackgroundColor(ContextCompat.getColor(MusicApp.sContext, R.color.colorPrimary));
        } else {
            mBNavigation.setBackgroundColor(ContextCompat.getColor(MusicApp.sContext, R.color.sdcound_primary));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mViewPager.setCurrentItem(item.getOrder());
        return true;
    }

    @Override
    public void tabLayoutJamendo() {
    }

    private Badge mRedTabBadge;

    private ViewPager mViewPager;

    private void initView(View view) {
        mViewPager = view.findViewById(R.id.main_viewpager);

        MainPageAdapter homePageAdapter = new MainPageAdapter(getContext(), getChildFragmentManager());
        mViewPager.setAdapter(homePageAdapter);
        mViewPager.setOffscreenPageLimit(homePageAdapter.getCount());

        Router.getInstance().register(this);
        mBNavigation = view.findViewById(R.id.home_tablayout);
        mBNavigation.setupWithViewPager(mViewPager);

        if (MusicApp.isYTB() && MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
            mBNavigation.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        } else if (MusicApp.isYTB() && MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
            mBNavigation.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.sdcound_primary));
        } else {
            mBNavigation.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary2));
        }

//        if (Mp3App.sPreferences.getBoolean("DownloadNew", false)) {
//            showRedBadge();
//        }

    }

    private TabLayout mBNavigation;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Router.getInstance().unregister(this);
    }

//    @Override
//    public void showRedBadge() {
//        Mp3App.sPreferences.edit().putBoolean("DownloadNew", false).apply();
//        if (_mActivity.isFinishing() || mViewPager.getCurrentItem() == 1) {
//            LogUtil.e(TAG, "isFinishing getCurrentItem == 0");
//            return;
//        }
//        LogUtil.v(TAG, "showRedBadge>>>>>>");
//        if (mRedTabBadge != null) {
//            return;
//        }
//        View itemView = ((ViewGroup)mBNavigation.getChildAt(0)).getChildAt(2);
//        mRedTabBadge = new QBadgeView(Mp3App.sContext)
//                .bindTarget(itemView);
//
//        mRedTabBadge.setBadgeBackgroundColor(ContextCompat.getColor(Mp3App.sContext,
//                    R.color.color2_fbc02d));
//
//        mRedTabBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
//        mRedTabBadge.setBadgeNumber(-1);
//        mRedTabBadge.setGravityOffset(16, true);
//    }

//    @Override
//    public void hideRedBadge() {
//        if (mRedTabBadge != null) {
//            mRedTabBadge.hide(true);
//            mRedTabBadge = null;
//        }
//    }
}
