package com.sjy.butterknife;

import android.app.Activity;
import android.view.View;

/**
 * Act的查找器
 */
public class ActivityViewFinder implements MyViewFinder {
    @Override
    public View findView(Object obj, int id) {
        return ((Activity) obj).findViewById(id);
    }
}
