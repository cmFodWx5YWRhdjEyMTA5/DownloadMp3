package com.wedownloader.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wedownloader.MusicApp;
import com.wedownloader.R;
import com.wedownloader.bean.YTbeBean;
import com.wedownloader.data.IMusicApi;
import com.wedownloader.data.soundcloud.SoundCloudApi;
import com.wedownloader.util.AdViewWrapperAdapter;
import com.wedownloader.util.Constants;
import com.wedownloader.facebook.FBAdUtils;
import com.wedownloader.util.FormatUtil;
import com.wedownloader.util.Utils;
import com.wedownloader.view.DownloadBottomSheetDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.NativeAd;
import com.wedownloader.data.Song;
import com.wedownloader.data.jamendo.JamendoApi;
import com.wedownloader.data.youtube.YouTubeApi;
import com.wedownloader.router.Router;
import com.wedownloader.facebook.FacebookReport;
import com.wedownloader.util.LogUtil;
import com.paginate.Paginate;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class SearchFragment extends SupportFragment implements ISearchFragment{

    private static final String TAG = "SearchFragment";

    private String mCurrentQuery;
    private RecyclerView mRecyclerView;
    private TextView mStatusTV;
    private Paginate mPaginate;
    private boolean isLoading;
    private boolean isLoaded = true;
    private View mLoadingView;

    private AsyncTask mLoadTask;

    private ArrayList<Song> mArrayList = new ArrayList<>();

    private IMusicApi mMusicApi;

    private AdViewWrapperAdapter mAdViewWrapperAdapter;

    @Override
    public void switchYouTubeSearch() {
        initApiClient();
    }

    @Override
    public void switchSoundCloudSearch() {
        initApiClient();
    }

    public static SearchFragment newInstance(String query) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("query", query);
        searchFragment.putNewBundle(bundle);
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    private void initApiClient() {
        if (MusicApp.isYTB()) {
            if (MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
                mMusicApi = new YouTubeApi();
            } else if (MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
                mMusicApi = new SoundCloudApi();
            }
        } else if (MusicApp.isSCloud()) {
            mMusicApi = new SoundCloudApi();
        } else if (MusicApp.isSingYTB()){
            mMusicApi = new YouTubeApi();
        } else {
            mMusicApi = new JamendoApi();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        LogUtil.v(TAG, "SearchFragment onCreateView");

        Router.getInstance().register(this);

        if (getArguments() != null) {
            mCurrentQuery = getArguments().getString("query");
        }

        initView(view);
        mLoadingView.setVisibility(View.VISIBLE);

        initApiClient();

        initData();

        FacebookReport.logSentSearchPageShow();

        FacebookReport.logSentSearchPage(mCurrentQuery);

        return view;
    }

    private CommonAdapter mCommonAdapter = new CommonAdapter<Song>(MusicApp.sContext,
            R.layout.list_item, mArrayList) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_thumbnail)
                .error(R.drawable.default_thumbnail);

        @Override
        protected void convert(ViewHolder holder, final Song baseModel, int position) {
            ImageView itemThumbnialIV = holder.getView(R.id.itemThIV);
            Glide.with(_mActivity).load(baseModel.getImageUrl()).apply(options).into(itemThumbnialIV);

            TextView titleTV = holder.getView(R.id.itemTitleView);
            titleTV.setText(baseModel.getName());

            TextView textTV = holder.getView(R.id.itemTextView);
            textTV.setText(baseModel.getArtistName());

            TextView timeTV = holder.getView(R.id.time_tv);
            if (!(baseModel instanceof YTbeBean.YTBSnippet)) {
                timeTV.setVisibility(View.VISIBLE);
                timeTV.setText(FormatUtil.formatMusicTime(baseModel.getDuration()));
            } else {
                timeTV.setVisibility(View.INVISIBLE);
            }

            holder.setOnClickListener(R.id.list_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadBottomSheetDialog.newInstance(baseModel)
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });
        }
    };

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.search_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicApp.sContext));
        mAdViewWrapperAdapter = new AdViewWrapperAdapter(mCommonAdapter);
        mRecyclerView.setAdapter(mAdViewWrapperAdapter);
        mStatusTV = view.findViewById(R.id.status_iv);

        mPaginate = Paginate.with(mRecyclerView, mCallbacks)
                .setLoadingTriggerThreshold(2)
                .build();
        mPaginate.setHasMoreDataToLoad(false);
        isLoaded = false;

        mLoadingView = view.findViewById(R.id.loading_pb);

        FBAdUtils.interstitialLoad(Constants.CHA_YE_ID, new FBAdUtils.FBInterstitialAdListener(){
            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                FBAdUtils.destoryInterstitial();
            }
        });
    }

    Paginate.Callbacks mCallbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            initData();
        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            return isLoaded;
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
        Router.getInstance().unregister(this);

        if (FBAdUtils.isInterstitialLoaded()) {
            FBAdUtils.showInterstitial();
        }
        FBAdUtils.destoryInterstitial();

        FBAdUtils.loadAd(Constants.NATIVE_ID_LIST);
    }

    private void initData() {
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
        mLoadTask = new AsyncTask<Void, Void, List<Song>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoading = true;
                mRecyclerView.setVisibility(View.VISIBLE);
                mStatusTV.setVisibility(View.GONE);
            }

            @Override
            protected List<Song> doInBackground(Void... integers) {
                if (mMusicApi != null) {
                    return mMusicApi.searchMusic(MusicApp.sContext, mCurrentQuery);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Song> arrayList) {
                super.onPostExecute(arrayList);
                if (_mActivity.isFinishing()) {
                    return;
                }

                isLoading = false;
                isLoaded = false;
                mLoadingView.setVisibility(View.GONE);

                if (arrayList == null) {
                    showErrorView();
                    return;
                } else if (arrayList.size() == 0) {
                    showEmptyView();
                    return;
                }

                NativeAd nativeAd = FBAdUtils.nextNativieAd();
                if (nativeAd == null || !nativeAd.isAdLoaded()) {
                    nativeAd = FBAdUtils.getNativeAd();
                }

                if (nativeAd != null && nativeAd.isAdLoaded() && arrayList.size() > 3) {
                    int offsetStart = mAdViewWrapperAdapter.getItemCount();
                    mAdViewWrapperAdapter.addAdView(offsetStart + 2, new AdViewWrapperAdapter.
                            AdViewItem(FBAdUtils.setUpItemNativeAdView(_mActivity, nativeAd), offsetStart + 2));
                }

                int positionStart = mAdViewWrapperAdapter.getItemCount();
                mArrayList.addAll(arrayList);
                mCommonAdapter.notifyItemRangeInserted(positionStart, arrayList.size());

                if (mMusicApi != null && !mMusicApi.onShowNextPage()) {
                    mPaginate.setHasMoreDataToLoad(false);
                    isLoaded = true;
                } else {
                    mPaginate.setHasMoreDataToLoad(true);
                }
            }
        }.executeOnExecutor(Utils.sExecutorService2);
    }

    private void showErrorView() {
        mRecyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(MusicApp.sContext, R.drawable.ic_error);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.network2_error2);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(MusicApp.sContext, R.drawable.ic_empty);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.empty_error);
    }

    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        mCurrentQuery = args.getString("query");
        LogUtil.v(TAG, "SearchFragment onNewBundle " + mCurrentQuery);
        if (mMusicApi != null) {
            mMusicApi.resetPaging();
        }

        mLoadingView.setVisibility(View.VISIBLE);
        mPaginate.setHasMoreDataToLoad(false);
        isLoaded = true;

        mArrayList.clear();
        mAdViewWrapperAdapter.clearAdView();
        mAdViewWrapperAdapter.notifyDataSetChanged();

        initData();

        FacebookReport.logSentSearchPage(mCurrentQuery);
    }
}
