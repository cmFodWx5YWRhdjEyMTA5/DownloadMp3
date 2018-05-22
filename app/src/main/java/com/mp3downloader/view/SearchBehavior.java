package com.mp3downloader.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.mp3downloader.util.LogUtil;

/**
 * Created by liyanju on 2018/5/22.
 */

public class SearchBehavior extends CoordinatorLayout.Behavior<FloatingSearchView> {

    public SearchBehavior() {
        super();
    }

    public SearchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingSearchView child, View dependency) {
        LogUtil.v("xx", "xxxxxx layoutDependsOn1111 " + dependency);
        if (dependency instanceof AppBarLayout) {
            ViewCompat.setElevation(child, ViewCompat.getElevation(dependency));
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingSearchView child, View dependency) {
        LogUtil.v("xx", "xxxxxx onDependentViewChanged2222 " + dependency.getY() + " dependency " + dependency);
        if (dependency instanceof AppBarLayout) {
            child.setTranslationY(dependency.getY());
            return true;
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }

}
