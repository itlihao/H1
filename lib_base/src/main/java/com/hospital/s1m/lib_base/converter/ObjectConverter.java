package com.hospital.s1m.lib_base.converter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSON;
import com.billy.cc.core.component.CC;
import com.lzy.okgo.convert.Converter;
import com.hospital.s1m.lib_base.BaseApplication;
import com.hospital.s1m.lib_base.R;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.entity.HttpResult;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.view.MineDialog;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
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
            if ("2010109".equals(code)) {
                Observable.empty().observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                //添加"Yes"按钮
                                //添加取消
                                Activity curActivity = BaseApplication.getCurActivity();
                                AlertDialog alertDialog2 = new MineDialog.Builder(curActivity)
                                        .setMessage(R.string.basic_return)
                                        .setPositiveButton(R.string.basic_comfirm, (dialogInterface, i) -> {
                                            CacheDataSource.clearCache();
                                            CC.obtainBuilder("lib_user.ComponentUser")
                                                    .setActionName("getLogin")
                                                    .build().call();
                                            curActivity.finish();
                                        }).create();
                                alertDialog2.show();
                                alertDialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (KeyEvent.KEYCODE_BACK == keyCode) {
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                                alertDialog2.setCancelable(false);
                            }
                        }).subscribe();
                return null;
            } else {
                Observable.empty()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> mListener.onFailed(code, mHttpResult.getBody().getContent()))
                        .subscribe();
                return null;
            }
        }
        return mListener.convert(mHttpResult.getBody().getContent());
    }
}
