package com.sjy.butterknife;

/**
 * act中绑定/解绑的接口
 *
 * 该接口通过反射，给javapoet用，用于生成java代码
 *
 * @param <T>
 */
public interface MyViewBinder<T> {
    void bindView(T host, Object obj, MyViewFinder viewFind);

    void unbindView(T host);
}
