package com.freedownloader.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.freedownloader.ui.HomeFragment;
import com.freedownloader.view.CircleImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by liyanju on 2018/8/11.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).apply(HomeFragment.requestOptions).into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        RoundedImageView roundedImageView = new RoundedImageView(context);
        roundedImageView.setCornerRadius(Utils.dip2px(context, 4));
        return roundedImageView;
    }
}
