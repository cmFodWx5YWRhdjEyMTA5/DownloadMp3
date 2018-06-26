package com.freedownloader.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.freedownloader.Mp3App;
import com.freedownloader.R;
import com.freedownloader.facebook.FBAdUtils;
import com.freedownloader.data.MusicSuggistion;
import com.freedownloader.router.Router;
import com.freedownloader.util.Constants;
import com.freedownloader.facebook.FacebookReport;
import com.freedownloader.util.LogUtil;
import com.freedownloader.util.Utils;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivity;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends SupportActivity {

    private FloatingSearchView mSearchView;
    private static final String TAG = "MainActivity";

    private boolean isSearched = false;

    private Badge mRedMenuBadge;

    public static boolean sIsInActivity;

    private void initSearchView() {
        mSearchView = findViewById(R.id.floating2_search_view);

        mSearchView.setSearchHint(getString(R.string.app_name));

        if (Mp3App.isYTB() && Mp3App.sPreferences.getBoolean("ShowRed", true)) {
            updateSearchMenu();
            mRedMenuBadge = new QBadgeView(Mp3App.sContext)
                    .bindTarget(findViewById(com.arlib.floatingsearchview.R.id.menu_view));
            mRedMenuBadge.setBadgeBackgroundColor(ContextCompat.getColor(Mp3App.sContext,
                    R.color.colorPrimary));
            mRedMenuBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
            mRedMenuBadge.setBadgeNumber(-1);
            mRedMenuBadge.setGravityOffset(6, true);
        }

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                onSearchAction(searchSuggestion.getBody());
                mSearchView.clearSearchFocus();
                mSearchView.setSearchText(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                LogUtil.e(TAG, "onSearchAction>>");
                isSearched = true;
                mSearchView.clearSuggestions();
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }
                mSearchView.hideProgress();

                start(SearchFragment.newInstance(currentQuery), ISupportFragment.SINGLETOP);
            }
        });
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageResource(R.drawable.ic_search_6060_24dp);
                textView.setText(item.getBody());
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                LogUtil.e(TAG, ">> isSearched " + isSearched);
                if (isSearched) {
                    isSearched = false;
                    return;
                }
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    searchSuggestions(newQuery);
                }
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (mRedMenuBadge != null) {
                    Mp3App.sPreferences.edit().putBoolean("ShowRed", false).apply();
                    mSearchView.post(new Runnable() {
                        @Override
                        public void run() {
                            mRedMenuBadge.hide(true);
                            mRedMenuBadge = null;
                        }
                    });
                }
                switch (item.getItemId())  {
                    case R.id.action_youtube:
                        mSearchView.inflateOverflowMenu(R.menu.search_menu3);
                        setSearchType(YOUTUBE_TYPE);

                        try {
                            Router.getInstance().getReceiver(ISearchFragment.class)
                                    .switchYouTubeSearch();
                            Router.getInstance().getReceiver(IHotFragment.class)
                                    .switchYouTubeSearch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setMainTopBarBg(true);
                        Utils.showLongToastSafe(R.string.switch_ytb_search);
                        break;
                    case R.id.action_rating:
                        Utils.gotoGP(MainActivity.this);
                        break;
                    case R.id.action_declare:
                        showDisclaimers();
                        break;
                    case R.id.action_soundcloud:
                        mSearchView.inflateOverflowMenu(R.menu.search_menu2);
                        setSearchType(SOUNDClOUND_TYPE);

                        try {
                            Router.getInstance().getReceiver(ISearchFragment.class)
                                    .switchSoundCloudSearch();
                            Router.getInstance().getReceiver(IHotFragment.class)
                                    .switchSoundCloudSearch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setMainTopBarBg(false);

                        Utils.showLongToastSafe(R.string.switch_sc_search);
                        break;
                    case R.id.action_share:
                        try {
                            Intent textIntent = new Intent(Intent.ACTION_SEND);
                            textIntent.setType("text/plain");
                            textIntent.putExtra(Intent.EXTRA_TEXT,
                                    String.format(getString(R.string.share_content), getPackageName()));
                            startActivity(Intent.createChooser(textIntent, getString(R.string.share_text)));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparence(this);
        setContentView(R.layout.activity_main);

        Utils.checkAndRequestPermissions(this);

        sIsInActivity = true;

        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }

        FBAdUtils.showAdDialog(this, Constants.NATIVE_ID_DIALOG);

        initSearchView();

        if (Mp3App.sPreferences.getBoolean("isReceiverRefer", true)) {
            mSearchView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Mp3App.sPreferences.edit().putBoolean("isReceiverRefer", false).apply();
                }
            }, 1000);
        }

        FacebookReport.logSentMainPageShow();

        initTopBarBG();
    }

    private void initTopBarBG() {
        mStatuBarView = findViewById(R.id.status_bar_view2);
        mTopBarLinear = findViewById(R.id.top_bar_linear2);

        if (Mp3App.isYTB() && getSearchType() == YOUTUBE_TYPE) {
            setMainTopBarBg(true);
        } else if (Mp3App.isYTB() && getSearchType() == SOUNDClOUND_TYPE) {
            setMainTopBarBg(false);
        } else {
            mStatuBarView.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.colorPrimary2));
            mTopBarLinear.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.colorPrimary2));
        }
    }

    @Override
    public void onBackPressedSupport() {
        LogUtil.v(TAG, "onBackPressedSupport");
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            moveTaskToBack(true);
            FBAdUtils.loadFBAds(Constants.NATIVE_ID_DIALOG);
        }
    }

    private View mStatuBarView;
    private View mTopBarLinear;

    private void setMainTopBarBg(boolean isYoutube) {
        if (isYoutube) {
            mStatuBarView.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.colorPrimary));
            mTopBarLinear.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.colorPrimary));
            try {
                Router.getInstance().getReceiver(IHomeFragment.class).tabLayoutBg(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mStatuBarView.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.sdcound_primary));
            mTopBarLinear.setBackgroundColor(ContextCompat
                    .getColor(Mp3App.sContext, R.color.sdcound_primary));
            try {
                Router.getInstance().getReceiver(IHomeFragment.class).tabLayoutBg(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDisclaimers() {
        if (MainActivity.this.isFinishing()) {
            return;
        }

        new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.disclaimers)
                .content(R.string.disclaimer_text)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_text).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).build().show();
    }

    public static final int SOUNDClOUND_TYPE = 1;
    public static final int YOUTUBE_TYPE = 2;

    public static void setSearchType(int type) {
        Mp3App.sPreferences.edit().putInt("searchType", type).apply();
    }

    public static int getSearchType() {
        return Mp3App.sPreferences.getInt("searchType", YOUTUBE_TYPE);
    }

    private void updateSearchMenu() {
        try {
            Field menuId = FloatingSearchView.class.getDeclaredField("mMenuId");
            menuId.setAccessible(true);
            if (getSearchType() == YOUTUBE_TYPE) {
                menuId.set(mSearchView, R.menu.search_menu3);
            } else {
                menuId.set(mSearchView, R.menu.search_menu2);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private AsyncTask mSearchTask;

    private void searchSuggestions(String newText) {
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }

        mSearchTask = new AsyncTask<String, Void, List<MusicSuggistion>>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mSearchView != null) {
                    mSearchView.showProgress();
                }
            }

            @Override
            protected List<MusicSuggistion> doInBackground(String... strings) {
                try {
                    LogUtil.v(TAG, "doInBackground suggistion");
                    String query = strings[0];
                    URL url = new URL("http://suggestqueries.google.com/complete/search?client=firefox&hl=fr&q="+query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        baos.close();
                        is.close();
                        byte[] byteArray = baos.toByteArray();
                        String content = new String(byteArray);
                        LogUtil.v(TAG, "searchSuggestions content::" + content);
                        if (!TextUtils.isEmpty(content)) {
                            JSONArray jsonArray = new JSONArray(content);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.optJSONArray(i);
                                if (jsonArray1 != null) {
                                    ArrayList<MusicSuggistion> list = new ArrayList<>();
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String str = jsonArray1.getString(j);
                                        list.add(new MusicSuggistion(str));
                                    }
                                    return list;
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mSearchView.hideProgress();
            }

            @Override
            protected void onPostExecute(List<MusicSuggistion> list) {
                super.onPostExecute(list);
                if (list != null && !isFinishing()) {
                    mSearchView.swapSuggestions(list);
                }
                mSearchView.hideProgress();
            }
        }.executeOnExecutor(Utils.sExecutorService, newText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }
        FBAdUtils.loadFBAds(Constants.NATIVE_ID_LIST);
    }
}
