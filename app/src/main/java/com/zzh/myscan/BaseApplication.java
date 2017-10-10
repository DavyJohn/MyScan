package com.zzh.myscan;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by 腾翔信息 on 2017/10/9.
 */

public class BaseApplication extends Application{
    private static BaseApplication mcontext;
    private static BaseApplication instance;
    public static int mNetWorkState = -1;
    private static Handler mHandler;
    private static Thread mMainThread;
    private static long		mMainThreadId;
    private static Looper mMainThreadLooper;

    private static List<Activity> activities = new ArrayList<Activity>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        mHandler = new Handler();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mMainThreadLooper = getMainLooper();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static Handler getHandler()
    {
        return mHandler;
    }

    public static Thread getMainThread()
    {
        return mMainThread;
    }

    public static long getMainThreadId()
    {
        return mMainThreadId;
    }

    public static Looper getMainThreadLooper()
    {
        return mMainThreadLooper;
    }

    public BaseApplication() {

    }

    public static BaseApplication getInstance() {
        if (instance == null)
            instance = new BaseApplication();
        return instance;
    }


    public static BaseApplication getAppContext() {
        return mcontext;
    }


    public void add(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    /**
     * 销毁当前集合中所有的Activity。
     */
    public void finishAll() {
        try {
            for (Activity activity : activities) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public String getDiskCacheDir(Context context){
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())||!Environment.isExternalStorageRemovable()){
            cachePath = context.getExternalCacheDir().getPath();
        }else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

}
