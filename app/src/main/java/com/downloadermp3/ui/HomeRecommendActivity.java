package com.downloadermp3.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.bean.MusicArchiveModel;
import com.downloadermp3.bean.TitleModel;
import com.downloadermp3.data.Song;
import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.util.AdViewWrapperAdapter;
import com.downloadermp3.util.FormatUtil;
import com.downloadermp3.util.LogUtil;
import com.downloadermp3.util.Utils;
import com.downloadermp3.view.DownloadBottomSheetDialog;
import com.facebook.ads.NativeAd;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liyanju on 2018/6/21.
 */

public class HomeRecommendActivity extends AppCompatActivity {

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, HomeRecommendActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    Toolbar toolbar;

    TabLayout tabs;

    ViewPager viewPager;

    private ArrayList<Song> featuerList;
    private ArrayList<Song> recentList;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.v("HomeRecomd", "onSaveInstanceState featuerList >>" + featuerList);
        if (featuerList != null) {
            outState.putParcelableArrayList("featuerList", featuerList);
        }
        if (recentList != null) {
            outState.putParcelableArrayList("recentList", recentList);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_recommed_layout);
        toolbar = findViewById(R.id.toolbar);
        Utils.setViewBackgroud(toolbar);
        Utils.setActivityStatusColor(this);
        tabs = findViewById(R.id.tabs);
        Utils.setViewBackgroud(tabs);
        viewPager = findViewById(R.id.viewpager);

        setUpToolbar();

        if (savedInstanceState != null) {
            featuerList = savedInstanceState.getParcelableArrayList("featuerList");
            recentList = savedInstanceState.getParcelableArrayList("recentList");
        } else {
            HashMap<Integer, MusicArchiveModel> map = (HashMap<Integer, MusicArchiveModel>) HomeFragment
                    .getDataByType(TitleModel.RECOMMEND_TYPE);
            LogUtil.v("HomeRecomd", "onSaveInstanceState  "+ map);
            if (map == null
                    || map.get(MusicArchiveModel.FEATURED_TYPE) == null
                    || map.get(MusicArchiveModel.RECENT_TYPE) == null) {
                finish();
                return;
            }
            featuerList = map.get(MusicArchiveModel.FEATURED_TYPE).contentList;
            recentList = map.get(MusicArchiveModel.RECENT_TYPE).contentList;
        }



        if (featuerList == null || recentList == null) {
            finish();
            return;
        }

        viewPager.setAdapter(new RecomFragmentAdapter(getSupportFragmentManager(), featuerList, recentList));
        tabs.setupWithViewPager(viewPager);
    }

    class RecomFragmentAdapter extends FragmentPagerAdapter {

        private String title[] = new String[]{Mp3App.sContext.getString(R.string.featuer_text),
                Mp3App.sContext.getString(R.string.recent_text)};
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public RecomFragmentAdapter(FragmentManager fm, ArrayList<Song> featuerList,
                                    ArrayList<Song> recentList) {
            super(fm);
            fragments.add(RecomFragment.newInstances(featuerList, true));
            fragments.add(RecomFragment.newInstances(recentList, false));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.recommend_text));
    }

    public static class RecomFragment extends Fragment {

        private ArrayList<Song> mList;

        private FragmentActivity activity;

        private boolean feature;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            activity = getActivity();
        }

        public static RecomFragment newInstances(ArrayList<Song> list, boolean feature) {
            RecomFragment recomFragment = new RecomFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", list);
            bundle.putBoolean("feature", feature);
            recomFragment.setArguments(bundle);
                       return recomFragment;
        }

       @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return LayoutInflater.from(getActivity()).inflate(R.layout.home_recom_fragment, null);
        }

        private AdViewWrapperAdapter adViewWrapperAdapter;

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_thumbnail)
                .error(R.drawable.default_thumbnail);

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Bundle bundle = getArguments();
            if (bundle != null) {
                mList = bundle.getParcelableArrayList("list");
                feature = bundle.getBoolean("feature");
            }

            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            CommonAdapter<Song> commonAdapter = new CommonAdapter<Song>(getActivity(), R.layout.list_item, mList) {
                @Override
                protected void convert(final ViewHolder holder, final Song song, int position) {
                    ImageView imageView = holder.getView(R.id.itemThIV);
                    Glide.with(activity).load(song.getImageUrl()).apply(options).into(imageView);

                    TextView titleTV = holder.getView(R.id.itemTitleView);
                    titleTV.setText(song.getName());

                    TextView textTV = holder.getView(R.id.itemTextView);
                    textTV.setText(song.getArtistName());

                    TextView timeTV = holder.getView(R.id.time_tv);
                    if (song.getDuration() != 0) {
                        timeTV.setText(FormatUtil.formatMusicTime(song.getDuration()));
                        timeTV.setVisibility(View.VISIBLE);
                    } else {
                        timeTV.setVisibility(View.INVISIBLE);
                    }

                    holder.getConvertView().setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            DownloadBottomSheetDialog.newInstance(song).showBottomSheetFragment(getChildFragmentManager());
                        }
                    });

                }
            };

            adViewWrapperAdapter = new AdViewWrapperAdapter(commonAdapter);

            if (feature && mList != null && mList.size() > 3) {
                NativeAd nativeAd = FBAdUtils.nextNativieAd();
                if (nativeAd != null && nativeAd.isAdLoaded()&& !adViewWrapperAdapter.isAddAdView()) {
                    adViewWrapperAdapter.addAdView(22, new AdViewWrapperAdapter.
                            AdViewItem(FBAdUtils.setUpItemNativeAdView(activity, nativeAd), 1));
                    recyclerView.setAdapter(adViewWrapperAdapter);
                    return;
                }
            }

            recyclerView.setAdapter(adViewWrapperAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
