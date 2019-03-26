package com.hospital.s1m.lib_base.converter;

import com.alibaba.fastjson.JSON;
import com.hospital.s1m.lib_base.entity.HttpResult;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.lzy.okgo.convert.Converter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 10295 on 2017/12/18 0018
 */

public class ObjectConverter<T> implements Converter<T> {
    private final ResponseListener<T> mListener;

    public ObjectConverter(ResponseListener<T> listener) {
        this.mListener = listener;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null || mListener == null) {
            return null;
        }
        HttpResult mHttpResult = JSON.parseObject(body.string(), HttpResult.class);
        String code = (String) mHttpResult.getBody().getCode();
        if (!"2000000".equals(code)) {
            Observable.empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> mListener.onFailed(code, mHttpResult.getBody().getContent(), mHttpResult.getBody().getPeriodType()))
                    .subscribe();
            return null;
        }
        return mListener.convert(mHttpResult.getBody().getContent());
    }
}
