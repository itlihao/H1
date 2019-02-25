package com.hospital.s1m.lib_base.listener;

/**
 * Created by admin on 2016/7/28.
 * ResponseListener
 */
public interface ResponseListener<T> {
    /**
     * 我运行在子线程
     */
    T convert(String jsonStr);

    void onSuccess(T result);

    void onFailed(String errorCode, String errorInfo);
}
