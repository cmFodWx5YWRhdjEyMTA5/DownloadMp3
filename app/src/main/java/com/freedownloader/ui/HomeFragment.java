package com.freedownloader.ui;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.freedownloader.Mp3App;
import com.freedownloader.R;
import com.freedownloader.bean.JamendoBean;
import com.freedownloader.bean.MusicArchiveBean;
import com.freedownloader.bean.TitleBean;
import com.freedownloader.data.HomeDataList;
import com.freedownloader.util.LogUtil;
import com.freedownloader.util.Utils;
import com.freedownloader.view.DownloadBottomSheetDialog;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/6/20.
 */

public class HomeFragment extends SupportFragment {

    private static ArrayList<Object> sDatas = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private int itemWidth;

    private MultiItemTypeAdapter adapter;

    private TextView mStatusTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemWidth = (Utils.getScreenWhith() - Utils.dip2px(getContext(), 2) * 4) / 3;
        swipeRefreshLayout = view.findViewById(R.id.home_swipeRefresh);
        swipeRefreshLayout.setProgressViewOffset(true, 0,Utils.dip2px(getContext(), 60));
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_mActivity, R.color.colorPrimary2),
                ContextCompat.getColor(_mActivity, R.color.colorPrimary),
                ContextCompat.getColor(_mActivity, R.color.sdcound_primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(false);
            }
        });

        mStatusTV = view.findViewById(R.id.status_iv);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MultiItemTypeAdapter(_mActivity, sDatas);
        adapter.addItemViewDelegate(new TitleItemDelagate());
        adapter.addItemViewDelegate(new JamendoGridGridItemDelagate());
        adapter.addItemViewDelegate(new JamendoGridGridItemDelagate2());
        adapter.addItemViewDelegate(new MusicArchListItemDelagate());

        recyclerView.setAdapter(adapter);

        initData(true);

    }

    private void showErrorView() {
        recyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(Mp3App.sContext, R.drawable.ic_error);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.network_error);
    }

    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(Mp3App.sContext, R.drawable.ic_empty);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.empty_error);
    }

    private AsyncTask loadTask;

    private void initData(boolean isNeedCache) {
        if (loadTask != null) {
            loadTask.cancel(true);
        }

        loadTask = new AsyncTask<Boolean, Void, ArrayList<Object>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LogUtil.v("initData", "onPreExecute");
                swipeRefreshLayout.setRefreshing(true);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            protected ArrayList<Object> doInBackground(Boolean... isNeedCache) {
                LogUtil.v("initData", "doInBackground");
                return HomeDataList.getHomeDataList(Mp3App.sContext, isNeedCache[0]);
            }

            @Override
            protected void onPostExecute(ArrayList<Object> objects) {
                super.onPostExecute(objects);
                LogUtil.v("initData", "onPostExecute");
                swipeRefreshLayout.setRefreshing(false);

                if ((objects == null || objects.size() == 0) && sDatas.size() != 0) {
                    Toast.makeText(_mActivity, R.string.load_error, Toast.LENGTH_LONG).show();
                    return;
                }

                if (objects == null) {
                    showErrorView();
                    return;
                }

                if (objects.size() == 0) {
                    showEmptyView();
                    return;
                }

                sDatas.clear();
                sDatas.addAll(objects);

                adapter.notifyDataSetChanged();
            }
        }.executeOnExecutor(Utils.sExecutorService3, isNeedCache);
    }

    RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.default_thumbnail_corners);

    class JamendoGridGridItemDelagate implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_item_grid_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            if (item instanceof ArrayList && ((ArrayList) item).get(0) instanceof JamendoBean.JamendoResult) {
                return true;
            }
            return false;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            final ArrayList<JamendoBean.JamendoResult> list = (ArrayList<JamendoBean.JamendoResult>) o;

            ImageView songImage1 = holder.getView(R.id.song_iv1);
            songImage1.getLayoutParams().height = itemWidth;
            Glide.with(getActivity()).load(list.get(0).image).apply(requestOptions)
                    .into(songImage1);
            TextView songName1 = holder.getView(R.id.song_title_tv1);
            songName1.setText(list.get(0).name);
            holder.setOnClickListener(R.id.list_item_linear1, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(0))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            ImageView songImage2 = holder.getView(R.id.song_iv2);
            songImage2.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(1).image).apply(requestOptions)
                    .into(songImage2);
            TextView songName2 = holder.getView(R.id.song_title_v2);
            songName2.setText(list.get(1).name);
            holder.setOnClickListener(R.id.list_item_linear2, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(1))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            ImageView songImage3 = holder.getView(R.id.song_image3);
            songImage3.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(2).image).apply(requestOptions)
                    .into(songImage3);
            TextView songName3 = holder.getView(R.id.song_name_tv3);
            songName3.setText(list.get(2).name);
            holder.setOnClickListener(R.id.list_item_linear3, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(2))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            ImageView songImage11 = holder.getView(R.id.song_image11);
            songImage11.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(3).image).apply(requestOptions)
                    .into(songImage11);
            TextView songName11 = holder.getView(R.id.song_name_tv11);
            songName11.setText(list.get(3).name);
            holder.setOnClickListener(R.id.list_item_linear11, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(3))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            ImageView songImage22 = holder.getView(R.id.song_image22);
            songImage22.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(4).image).apply(requestOptions).into(songImage22);
            TextView songName22 = holder.getView(R.id.song_name_tv22);
            songName22.setText(list.get(4).name);
            holder.setOnClickListener(R.id.list_item_linear22, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(4))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            ImageView songImage33 = holder.getView(R.id.song_image33);
            songImage33.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(5).image).apply(requestOptions).into(songImage33);
            TextView songName33 = holder.getView(R.id.song_name_tv33);
            songName33.setText(list.get(5).name);
            holder.setOnClickListener(R.id.list_item_linear33, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(list.get(5))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });
        }
    }

    class JamendoGridGridItemDelagate2 implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_item_grid_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            if (item instanceof ArrayList && ((ArrayList) item).get(0) instanceof JamendoBean) {
                return true;
            }
            return false;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            final ArrayList<JamendoBean> list = (ArrayList<JamendoBean>) o;

            ImageView songImage1 = holder.getView(R.id.song_iv1);
            songImage1.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(0).imageRes).apply(requestOptions).into(songImage1);
            TextView songName1 = holder.getView(R.id.song_title_tv1);
            songName1.setText(list.get(0).name);
            holder.setOnClickListener(R.id.list_item_linear1, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(0).type, list.get(0).name, list.get(0).tags);
                }
            });

            ImageView songImage2 = holder.getView(R.id.song_iv2);
            songImage2.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(1).imageRes).apply(requestOptions).into(songImage2);
            TextView songName2 = holder.getView(R.id.song_title_v2);
            songName2.setText(list.get(1).name);
            holder.setOnClickListener(R.id.list_item_linear2, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(1).type, list.get(1).name, list.get(1).tags);
                }
            });

            ImageView songImage3 = holder.getView(R.id.song_image3);
            songImage3.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(2).imageRes).apply(requestOptions).into(songImage3);
            TextView songName3 = holder.getView(R.id.song_name_tv3);
            songName3.setText(list.get(2).name);
            holder.setOnClickListener(R.id.list_item_linear3, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(2).type, list.get(2).name, list.get(2).tags);
                }
            });

            ImageView songImage11 = holder.getView(R.id.song_image11);
            songImage11.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(3).imageRes).apply(requestOptions).into(songImage11);
            TextView songName11 = holder.getView(R.id.song_name_tv11);
            songName11.setText(list.get(3).name);
            holder.setOnClickListener(R.id.list_item_linear11, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(3).type, list.get(3).name, list.get(3).tags);
                }
            });

            ImageView songImage22 = holder.getView(R.id.song_image22);
            songImage22.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(4).imageRes).apply(requestOptions).into(songImage22);
            TextView songName22 = holder.getView(R.id.song_name_tv22);
            songName22.setText(list.get(4).name);
            holder.setOnClickListener(R.id.list_item_linear22, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(4).type, list.get(4).name, list.get(4).tags);
                }
            });

            ImageView songImage33 = holder.getView(R.id.song_image33);
            songImage33.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(list.get(5).imageRes).apply(requestOptions).into(songImage33);
            TextView songName33 = holder.getView(R.id.song_name_tv33);
            songName33.setText(list.get(5).name);
            holder.setOnClickListener(R.id.list_item_linear33, new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    HomeListActivity.launch(_mActivity, list.get(5).type, list.get(5).name, list.get(5).tags);
                }
            });
        }
    }

    class MusicArchListItemDelagate implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_item_list_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof HashMap;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            final MusicArchiveBean featured = (MusicArchiveBean) (((HashMap) o)
                    .get(MusicArchiveBean.FEATURED_TYPE));

            final MusicArchiveBean recent = (MusicArchiveBean) (((HashMap) o)
                    .get(MusicArchiveBean.RECENT_TYPE));

            ImageView featuredIV = holder.getView(R.id.song_iv1);
            featuredIV.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity)
                    .load(featured.contentList.get(0).getImageUrl())
                    .apply(requestOptions).into(featuredIV);
            TextView featuredTV = holder.getView(R.id.song_title_tv1);
            featuredTV.setText(featured.contentList.get(0).getName());

            ImageView recentIV = holder.getView(R.id.song_iv2);
            recentIV.getLayoutParams().height = itemWidth;
            Glide.with(_mActivity).load(recent.contentList.get(0).getImageUrl()).apply(requestOptions).into(recentIV);
            TextView recentTV = holder.getView(R.id.song_title_v2);
            recentTV.setText(recent.contentList.get(0).getName());

            holder.setOnClickListener(R.id.list_item_linear1, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(featured.contentList.get(0))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });

            holder.setOnClickListener(R.id.list_item_linear2, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadBottomSheetDialog.newInstance(recent.contentList.get(0))
                            .showBottomSheetFragment(getChildFragmentManager());
                }
            });
        }
    }

    public static Object getDataByType(int type) {
        for (int i = 0; i < sDatas.size(); i++) {
            Object obj = sDatas.get(i);
            if (obj instanceof TitleBean
                    && ((TitleBean) obj).type == type) {
                return sDatas.get(i + 1);
            }
        }
        return null;
    }

    class TitleItemDelagate implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_item_title_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof TitleBean;
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            final TitleBean titleModel = (TitleBean) o;
            TextView titleTV = holder.getView(R.id.home_title_item_tv);
            titleTV.setText(titleModel.title);

            holder.getView(R.id.title_relative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (titleModel.type == TitleBean.GENRES_TYPE
                            || titleModel.type == TitleBean.INSTRUMENT_TYPE) {
                        HomeGridActivity.launch(_mActivity, titleModel.title, titleModel.type);
                    } else if (titleModel.type == TitleBean.RECOMMEND_TYPE) {
                        HomeRecommendActivity.launch(_mActivity);
                    } else {
                        HomeListActivity.launch(_mActivity, titleModel.type, titleModel.title);
                    }
                }
            });
        }
    }
}
