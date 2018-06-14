package com.downloadermp3.ui;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.bean.YTbeModel;
import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.router.Router;
import com.downloadermp3.util.FormatUtil;
import com.facebook.ads.NativeAd;
import com.downloadermp3.data.BaseModel;
import com.downloadermp3.data.IMusicApi;
import com.downloadermp3.data.jamendo.JamendoApi;
import com.downloadermp3.data.youtube.YouTubeApi;
import com.downloadermp3.util.AdViewWrapperAdapter;
import com.downloadermp3.view.DownloadBottomSheetDialog;
import com.downloadermp3.util.LogUtil;
import com.downloadermp3.util.Utils;
import com.paginate.Paginate;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class HotFragment extends SupportFragment implements IHotFragment{

    private ArrayList<BaseModel> mArrayList = new ArrayList<>();

    private TextView mStatusTV;
    private SmartRefreshLayout mSwipeRefreshLayout;
    private Paginate mPaginate;
    private RecyclerView mRecyclerView;

    private boolean isLoading;
    private boolean isLoaded = true;

    private AsyncTask mLoadTask;

    private IMusicApi mMusicApi;

    private static final String TAG = "HotFragment";

    private AdViewWrapperAdapter mAdViewWrapperAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hot_fragment, container, false);

        initApiClient();

        initView(view);

        return view;
    }

    private void initApiClient() {
        if (Mp3App.isYTB()) {
            mMusicApi = new YouTubeApi();
        } else {
            mMusicApi = new JamendoApi();
        }
    }

    private BezierRadarHeader bezierRadarHeader;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int color_bg = R.color.colorPrimary2;
        if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.YOUTUBE_TYPE) {
            color_bg = R.color.colorPrimary;
        } else if (Mp3App.isYTB() && MainActivity.getSearchType() == MainActivity.SOUNDClOUND_TYPE) {
            color_bg = R.color.sdcound_primary;
        }
        bezierRadarHeader = new BezierRadarHeader(_mActivity);
        bezierRadarHeader.setEnableHorizontalDrag(true);
        bezierRadarHeader.setPrimaryColor(ContextCompat.getColor(_mActivity, color_bg));
        mSwipeRefreshLayout.setRefreshHeader(bezierRadarHeader);
        mSwipeRefreshLayout.autoRefresh(400);

        Router.getInstance().register(this);
    }

    private CommonAdapter mCommonAdapter = new CommonAdapter<BaseModel>(Mp3App.sContext,
            R.layout.list_item, mArrayList) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_thumbnail)
                .error(R.drawable.default_thumbnail);

        @Override
        protected void convert(ViewHolder holder, final BaseModel baseModel, int position) {
            ImageView itemThumbnialIV = holder.getView(R.id.itemThIV);
            Glide.with(_mActivity).load(baseModel.getImageUrl()).apply(options).into(itemThumbnialIV);

            TextView titleTV = holder.getView(R.id.itemTitleView);
            titleTV.setText(baseModel.getName());

            TextView textTV = holder.getView(R.id.itemTextView);
            textTV.setText(baseModel.getArtistName());

            TextView timeTV = holder.getView(R.id.time_tv);
            if (!(baseModel instanceof YTbeModel.YTBSnippet)) {
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
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(Mp3App.sContext, R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                LogUtil.v(TAG, "onRefresh>>>>");
                if (mMusicApi != null) {
                    mMusicApi.resetPaging();
                }
                initData();
            }
        });

        mRecyclerView = view.findViewById(R.id.hot_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Mp3App.sContext));
        mAdViewWrapperAdapter = new AdViewWrapperAdapter(mCommonAdapter);
        mRecyclerView.setAdapter(mAdViewWrapperAdapter);
        mStatusTV = view.findViewById(R.id.status_iv);

        mPaginate = Paginate.with(mRecyclerView, mCallbacks)
                .setLoadingTriggerThreshold(2)
                .build();
        mPaginate.setHasMoreDataToLoad(false);
        isLoaded = false;
    }

    private void showErrorView() {
        mRecyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(Mp3App.sContext, R.drawable.ic_error);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.load_error);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(Mp3App.sContext, R.drawable.ic_empty);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.empty_error);
    }

    private void initData() {
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
        mLoadTask = new AsyncTask<Void, Void, List<BaseModel>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoading = true;
                mRecyclerView.setVisibility(View.VISIBLE);
                mStatusTV.setVisibility(View.GONE);
            }

            @Override
            protected List<BaseModel> doInBackground(Void... Void) {
                LogUtil.v(TAG, "doInBackground getRecommondMusic");
                if (mMusicApi != null) {
                    return mMusicApi.getRecommondMusic(Mp3App.sContext);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<BaseModel> arrayList) {
                super.onPostExecute(arrayList);
                if (_mActivity.isFinishing()) {
                    return;
                }

                isLoading = false;

                if (arrayList == null) {
                    mSwipeRefreshLayout.finishRefresh(false);
                    showErrorView();
                    return;
                } else if (arrayList.size() == 0 && mArrayList.size() == 0) {
                    mSwipeRefreshLayout.finishRefresh(false);
                    showEmptyView();
                    return;
                }

                if (arrayList == null || arrayList.size() == 0) {
                    mSwipeRefreshLayout.finishRefresh(false);
                    return;
                }

                NativeAd nativeAd = FBAdUtils.nextNativieAd();
                if (nativeAd == null || !nativeAd.isAdLoaded()) {
                    nativeAd = FBAdUtils.getNativeAd();
                }

                Collections.shuffle(arrayList);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.finishRefresh(true);
                    mArrayList.clear();
                    mAdViewWrapperAdapter.clearAdView();
                    mArrayList.addAll(arrayList);
                    mPaginate.setHasMoreDataToLoad(true);
                    if (nativeAd != null && nativeAd.isAdLoaded() && arrayList.size() > 3) {
                        mAdViewWrapperAdapter.addAdView(2, new AdViewWrapperAdapter.
                                AdViewItem(FBAdUtils.setUpItemNativeAdView(_mActivity, nativeAd), 2));
                    }
                    mAdViewWrapperAdapter.notifyDataSetChanged();
                    return;
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
        }.executeOnExecutor(Utils.sExecutorService);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
        Router.getInstance().unregister(this);
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
    public void switchYouTubeSearch() {
        if (bezierRadarHeader != null) {
            bezierRadarHeader.setPrimaryColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        }
    }

    @Override
    public void switchSoundCloudSearch() {
        if (bezierRadarHeader != null) {
            bezierRadarHeader.setPrimaryColor(ContextCompat.getColor(_mActivity, R.color.sdcound_primary));
        }
    }
}
