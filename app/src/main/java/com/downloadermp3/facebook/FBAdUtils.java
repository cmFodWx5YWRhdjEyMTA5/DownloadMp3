package com.downloadermp3.facebook;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.downloadermp3.R;
import com.downloadermp3.util.Utils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;
import com.downloadermp3.Mp3App;

/**
 * Created by liyanju on 2018/4/9.
 */

public class FBAdUtils {

    private static NativeAdsManager sAds;

    private static NativeAd sNativeAd;

    private static volatile boolean isLoadAding = false;

    private static Context sContext;

    private static InterstitialAd sInterstitialAd;

    public static void init(Context context) {
        sContext = context;
    }

    public static void interstitialLoad(String aid, final FBInterstitialAdListener listener) {
        sInterstitialAd = new InterstitialAd(sContext, aid);
        sInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                if (listener != null) {
                    listener.onInterstitialDisplayed(ad);
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (listener != null) {
                    listener.onInterstitialDismissed(ad);
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (listener != null) {
                    listener.onError(ad, adError);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (listener != null && sInterstitialAd != null) {
                    listener.onLoaded(sInterstitialAd);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (listener != null) {
                    listener.onAdClicked(ad);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (listener != null) {
                    listener.onLoggingImpression(ad);
                }
            }

        });
        sInterstitialAd.loadAd();
    }

    public static boolean isInterstitialLoaded() {
        return sInterstitialAd != null && sInterstitialAd.isAdLoaded();
    }

    public static void showInterstitial() {
        try {
            if (sInterstitialAd != null && sInterstitialAd.isAdLoaded() && Utils.isScreenOn()) {
                sInterstitialAd.show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void destoryInterstitial() {
        try {
            if (sInterstitialAd != null) {
                sInterstitialAd.destroy();
                sInterstitialAd = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void loadFBAds(String adid) {
        sAds = new NativeAdsManager(sContext, adid, 10);
        sAds.loadAds();
    }

    public static NativeAd nextNativieAd() {
        if (sAds != null && sAds.isLoaded()) {
            return sAds.nextNativeAd();
        }
        return null;
    }

    public static NativeAd getNativeAd() {
        return sNativeAd;
    }

    public static void loadAd(String adId) {
        loadAd(adId, null);
    }

    public static void loadAd(String adId, final AdListener adListener) {
        Log.v("facebook", "loadAd....." + isLoadAding);
        if (isLoadAding) {
            return;
        }
        isLoadAding = true;

        sNativeAd = new NativeAd(sContext, adId);

        sNativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.v("facebook", "onError....." + adError.getErrorMessage());
                if (adListener != null) {
                    adListener.onError(ad, adError);
                }
                isLoadAding = false;
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.v("facebook", "onAdLoaded.....");
                if (adListener != null) {
                    adListener.onAdLoaded(ad);
                }
                isLoadAding = false;
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (adListener != null) {
                    adListener.onAdClicked(ad);
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (adListener != null) {
                    adListener.onLoggingImpression(ad);
                }
            }
        });

        sNativeAd.loadAd();
    }

    public static void showAdDialog(Activity activity, String adId) {
        showAdDialog(activity, adId, null);
    }

    public static void showAdDialog(final Activity activity, final String adId,final Runnable errorCallBack) {
//        NativeAd nativeAd = nextNativieAd();
//        if (nativeAd != null && nativeAd.isAdLoaded()) {
//            View view = setupAdView(nativeAd);
//            showDialog(view, activity);
//            loadAd(adId, null);
//            return;
//        }

        loadAd(adId, new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                if (errorCallBack != null) {
                    errorCallBack.run();
                } else {
                    NativeAd nativeAd = nextNativieAd();
                    if (nativeAd != null && nativeAd.isAdLoaded()) {
                        View view = setupAdView(nativeAd);
                        showDialog(view, activity);
                     }
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (sNativeAd == null || !sNativeAd.isAdLoaded()) {
                    return;
                }
                View view = setupAdView(sNativeAd);
                showDialog(view, activity);
                loadAd(adId, null);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
    }

    private static void showDialog(View view, Activity activity) {
        if (view != null && activity != null && !activity.isFinishing()) {
            try {
                final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                        .canceledOnTouchOutside(true)
                        .customView(view, false).build();
                dialog.show();
                view.findViewById(R.id.ad_close_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static View setupAdView(NativeAd nativeAd) {
        try {
            View currentAdView = LayoutInflater.from(sContext)
                    .inflate(R.layout.big_ad_fb_layout, null);
            MediaView nativeAdMedia = currentAdView.findViewById(R.id.fb_half_mv_view);
            FrameLayout adChoicesFrame = currentAdView.findViewById(R.id.fb_adChoices_view);
            ImageView nativeAdIcon = currentAdView.findViewById(R.id.fb_half_icon_iv);
            TextView nativeAdTitle = currentAdView.findViewById(R.id.fb_banner_title_tv);
            TextView nativeAdBody = currentAdView.findViewById(R.id.fb_banner_desc_tv);
            TextView nativeAdCallToAction = currentAdView.findViewById(R.id.fb_half_actionbtn);
            currentAdView.findViewById(R.id.fb_half_iv).setVisibility(View.GONE);

            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            nativeAdTitle.setText(nativeAd.getAdTitle());
            nativeAdBody.setText(nativeAd.getAdBody());

            // Download and setting the cover image.
            NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
            nativeAdMedia.setNativeAd(nativeAd);

            // Downloading and setting the ad icon.
            NativeAd.Image adIcon = nativeAd.getAdIcon();
            Glide.with(Mp3App.sContext).load(adIcon.getUrl()).into(nativeAdIcon);

            // Add adChoices icon
            AdChoicesView adChoicesView = new AdChoicesView(sContext, nativeAd, true);
            adChoicesFrame.addView(adChoicesView, 0);
            adChoicesFrame.setVisibility(View.VISIBLE);

            nativeAd.registerViewForInteraction(nativeAdCallToAction);

            return currentAdView;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static View setUpItemNativeAdView(Activity activity, NativeAd nativeAd) {
        return setUpItemNativeAdView(activity, nativeAd, false);
    }

    public static View setUpItemNativeAdView(Activity activity, NativeAd nativeAd, boolean isSmallItem) {
        nativeAd.unregisterView();

        View adView = LayoutInflater.from(activity).inflate(R.layout.ad_list_fb_item, null);
        FrameLayout imageAdFrame = adView.findViewById(R.id.image2_ad_frame);

        ImageView nativeAdIcon;
        if (isSmallItem) {
            imageAdFrame.setVisibility(View.GONE);
            nativeAdIcon = adView.findViewById(R.id.image_ad2);
            nativeAdIcon.setVisibility(View.VISIBLE);
        } else {
            nativeAdIcon = adView.findViewById(R.id.image2_ad);
            imageAdFrame.setVisibility(View.VISIBLE);
            imageAdFrame.setBackground(ContextCompat.getDrawable(activity, R.drawable.fb_ad_item_bg));
            adView.findViewById(R.id.image_ad2).setVisibility(View.GONE);
        }

        FrameLayout adChoicesFrame = adView.findViewById(R.id.fb_adChoices2);
        TextView nativeAdTitle = adView.findViewById(R.id.title);
        TextView nativeAdBody = adView.findViewById(R.id.text);
        TextView nativeAdCallToAction = adView.findViewById(R.id.call_btn_tv);

        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdTitle.setText(nativeAd.getAdTitle());
        if (isSmallItem) {
            nativeAdBody.setText(nativeAd.getAdBody());
        }

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Add adChoices icon
        AdChoicesView adChoicesView = new AdChoicesView(sContext, nativeAd, true);
        adChoicesFrame.addView(adChoicesView, 0);
        adChoicesFrame.setVisibility(View.VISIBLE);

        nativeAd.registerViewForInteraction(nativeAdCallToAction);

        return adView;
    }

    public static class FBInterstitialAdListener implements InterstitialAdListener {

        public void onLoaded(InterstitialAd interstitialAd){

        }

        @Override
        public void onInterstitialDisplayed(Ad ad) {

        }

        @Override
        public void onInterstitialDismissed(Ad ad) {

        }

        @Override
        public void onError(Ad ad, AdError adError) {

        }

        @Override
        public void onAdLoaded(Ad ad) {

        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    }

}
