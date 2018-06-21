package com.downloadermp3.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.adapter.MainPageAdapter;
import com.downloadermp3.router.Router;
import com.downloadermp3.util.LogUtil;

import me.yokeyword.fragmentation.SupportFragment;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

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
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    hideRedBadge();
                }
                int menuId = mBNavigation.getMenu().getItem(position).getItemId();
                mBNavigation.setSelectedItemId(menuId);
            }
        });
        mViewPager.setOffscreenPageLimit(homePageAdapter.getCount());

        Router.getInstance().register(this);
        mBNavigation = view.findViewById(R.id.main_navigation);
        mBNavigation.setOnNavigationItemSelectedListener(this);

        int[][] states = new int[][]{                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors;
        if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
            colors = new int[]{ getResources().getColor(R.color.color_606060),
                    getResources().getColor(R.color.colorPrimary)
            };
        } else if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
            colors = new int[]{ getResources().getColor(R.color.color_606060),
                    getResources().getColor(R.color.sdcound_primary)
            };
        } else {
            colors = new int[]{ getResources().getColor(R.color.color_606060),
                    getResources().getColor(R.color.colorPrimary2)
            };
        }

        ColorStateList csl = new ColorStateList(states, colors);
        mBNavigation.setItemIconTintList(csl);
        mBNavigation.setItemTextColor(csl);

        if (Mp3App.sPreferences.getBoolean("DownloadNew", false)) {
            showRedBadge();
        }


    }

    private BottomNavigationView mBNavigation;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Router.getInstance().unregister(this);
    }

    @Override
    public void showRedBadge() {
        Mp3App.sPreferences.edit().putBoolean("DownloadNew", false).apply();
        if (_mActivity.isFinishing() || mViewPager.getCurrentItem() == 1) {
            LogUtil.e(TAG, "isFinishing getCurrentItem == 0");
            return;
        }
        LogUtil.v(TAG, "showRedBadge>>>>>>");
        if (mRedTabBadge != null) {
            return;
        }

        BottomNavigationMenuView view = (BottomNavigationMenuView)mBNavigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView)view.getChildAt(1);
        mRedTabBadge = new QBadgeView(Mp3App.sContext)
                .bindTarget(itemView);
        if (Mp3App.isYTB()) {
            mRedTabBadge.setBadgeBackgroundColor(ContextCompat.getColor(Mp3App.sContext,
                    R.color.colorPrimary));
        } else {
            mRedTabBadge.setBadgeBackgroundColor(ContextCompat.getColor(Mp3App.sContext,
                    R.color.colorPrimary2));
        }
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
