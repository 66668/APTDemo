package com.sjy.butterknife;

import android.view.View;

/**
 * ui查找器接口
 *
 * 该接口通过反射，给javapoet用，用于生成java代码
 */
public interface MyViewFinder {
    /**
     * 从 object中查找一个id的控件
     *
     * @param obj
     * @param id
     * @return
     */
    View findView(Object obj, int id);
}
