package com.mp3downloader.musicgo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mp3downloader.App;
import com.mp3downloader.R;
import com.mp3downloader.adapter.HomePageAdapter;
import com.mp3downloader.router.Router;
import com.mp3downloader.util.LogUtil;

import me.yokeyword.fragmentation.SupportFragment;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by liyanju on 2018/5/7.
 */

public class HomeFragment extends SupportFragment implements IHomeFragment{

    public static final String TAG = "HomeFragment";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    private TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        initView(view);
        return view;
    }

    private Badge mRedTabBadge;

    private ViewPager mViewPager;

    private void initView(View view) {
        mTabLayout = view.findViewById(R.id.home_tabs);
        mViewPager = view.findViewById(R.id.viewpager);

        HomePageAdapter homePageAdapter = new HomePageAdapter(getContext(), getChildFragmentManager());
        mViewPager.setAdapter(homePageAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    hideRedBadge();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        Router.getInstance().register(this);

        if (App.sPreferences.getBoolean("isNewDownload", false)) {
            showRedBadge();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Router.getInstance().unregister(this);
    }

    @Override
    public void showRedBadge() {
        App.sPreferences.edit().putBoolean("isNewDownload", false).apply();
        if (_mActivity.isFinishing() || mViewPager.getCurrentItem() == 1) {
            LogUtil.e(TAG, "isFinishing getCurrentItem == 0");
            return;
        }
        LogUtil.v(TAG, "showRedBadge>>>>>>");

        mRedTabBadge = new QBadgeView(App.sContext)
                .bindTarget(((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(1));
        mRedTabBadge.setBadgeBackgroundColor(ContextCompat.getColor(App.sContext,
                R.color.red));
        mRedTabBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
        mRedTabBadge.setBadgeNumber(-1);
        mRedTabBadge.setGravityOffset(16, true);
    }

    @Override
    public void hideRedBadge() {
        if (mRedTabBadge != null) {
            mRedTabBadge.hide(true);
            mRedTabBadge = null;
        }
    }
}
