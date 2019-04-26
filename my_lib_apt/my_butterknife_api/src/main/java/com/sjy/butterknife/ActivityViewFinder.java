package com.sjy.butterknife;

import android.app.Activity;
import android.view.View;

/**
 * Act的查找器
 * <p>
 * as点击rebuild后，会在app/build下生成对应代码，生成的代码会调用该方法
 */
public class ActivityViewFinder implements MyViewFinder {
    @Override
    public View findView(Object obj, int id) {
        return ((Activity) obj).findViewById(id);
    }
}
