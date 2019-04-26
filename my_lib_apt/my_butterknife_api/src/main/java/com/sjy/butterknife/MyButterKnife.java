package com.sjy.butterknife;

import android.app.Activity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 给act调用的api
 */
public class MyButterKnife {
    private static final ActivityViewFinder activityFinder = new ActivityViewFinder();//默认声明一个Activity View查找器
    private static final Map<String, MyViewBinder> binderMap = new LinkedHashMap<>();//MyViewBinder的缓存集合

    /**
     * Activity注解绑定 ActivityViewFinder
     *
     * @param activity
     */
    public static void bind(Activity activity) {
        bind(activity, activity, activityFinder);
    }

    /**
     * 注解绑定
     *
     * @param host   表示注解 View 变量所在的类，也就是注解类 进行绑定的目标对象
     * @param obj    表示查找 View 的地方，Activity & View 自身就可以查找，Fragment 需要在自己的 itemView 中查找
     * @param finder ui绑定提供者接口 这个用来统一处理Activity、View、Dialog等查找 View 和 Context 的方法
     */
    private static void bind(Object host, Object obj, ActivityViewFinder finder) {

        String className = host.getClass().getName();
        try {
            //看下对应的ViewBinder是否存在
            MyViewBinder binder = binderMap.get(className);
            if (binder == null) {
                //不存在则通过反射创建一个 然后存入缓存 这个类是通过javapoet生成的
                Class aClass = Class.forName(className + "$MyViewBinder");
                binder = (MyViewBinder) aClass.newInstance();
                binderMap.put(className, binder);
            }
            //把finder类跟使用注解类的 类 绑定
            if (binder != null) {
                binder.bindView(host, obj, finder);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除注解绑定 ActivityViewFinder
     *
     * @param host
     */
    public static void unBind(Object host) {
        String className = host.getClass().getName();
        MyViewBinder binder = binderMap.get(className);
        if (binder != null) {
            binder.unbindView(host);
        }
        binderMap.remove(className);
    }

}
