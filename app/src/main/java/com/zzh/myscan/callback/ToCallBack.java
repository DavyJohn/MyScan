package com.zzh.myscan.callback;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.Response;

/**
 * Created by 腾翔信息 on 2017/8/30.
 */

public abstract class ToCallBack<T> extends Callback<T>{
    public Class<T> subClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];;
    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        T data = (T) new Gson().fromJson(string,subClass);
        return data;
    }
}
