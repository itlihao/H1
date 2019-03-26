package com.hospital.s1m.lib_base.data;

import android.annotation.SuppressLint;
import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.hospital.s1m.lib_base.converter.ObjectConverter;
import com.hospital.s1m.lib_base.entity.Body;
import com.hospital.s1m.lib_base.entity.Header;
import com.hospital.s1m.lib_base.entity.HttpResult;
import com.hospital.s1m.lib_base.listener.MyDownloadListener;
import com.hospital.s1m.lib_base.listener.ResponseListener;
import com.hospital.s1m.lib_base.utils.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.exception.OkGoException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okrx2.adapter.ObservableBody;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.upload.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

/**
 * Created by wyl on 2017/6/8
 * 网络数据
 */

public class NetDataSource {
    // 查询起始页
    private static final int PAGE_START = 1;
    // 查询结束页
    private static final int PAGE_SIZE = 999;
    // 读取超时时间
    private static final int READ_TIMEOUT = 10;
    // 写入超时时间
    private static final int WRITE_TIMEOUT = 10;
    // 连接超时时间
    private static final int CONNECT_TIMEOUT = 10;
    // 请求重试次数
    private static final int RETRY_COUNT = 0;

    private static final HashMap<Object, CompositeDisposable> REQUEST_MAP = new HashMap<>();
    private static final HashMap<Object, ArrayList<Runnable>> TASKS_MAP = new HashMap<>();

    /**
     * 调用其他方法的前提
     *
     * @param application application
     * @param logSwitch   LOG开关
     * @param logTag      LOG标签
     */
    public static void init(Application application, boolean logSwitch, String logTag) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (logSwitch) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logTag);
            // log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            // log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }

        // 配置超时时间
        // 全局的读取超时时间
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        // 全局的写入超时时间
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        // 全局的连接超时时间
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);

        // 配置Cookie
        // 使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        // 配置Https
        // 信任所有证书
        // HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        // builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        // 配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        // builder.hostnameVerifier(new SafeHostnameVerifier());

        //必须调用初始化
        OkGo.getInstance()
                .init(application)
                //设置OkHttpClient
                .setOkHttpClient(builder.build())
                //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheMode(CacheMode.NO_CACHE)
                //全局统一缓存时间，默认永不过期，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .setRetryCount(RETRY_COUNT);
    }


    /**
     * 不需要opeCode就传0
     */
    public static <T, V> void post(Object tag, String url, V data, ResponseListener<T> listener) {
        HttpParams httpParams = new HttpParams();
        HttpResult<V> httpResult = new HttpResult<>();
        Header header = new Header();
        header.setImei(CacheDataSource.getImei());
        header.setType(CacheDataSource.getType());
        header.setV(CacheDataSource.getV());
        header.setUserToken(CacheDataSource.getUserToken());
        header.setClinicId(CacheDataSource.getClinicId());
        header.setDoctorMainId(CacheDataSource.getDoctorMainId());
        httpResult.setHeader(header);
        Body<V> body = new Body<>();
        body.setParam(data);
        httpResult.setBody(body);
        httpResult.getHeader().setCurrentTime(System.currentTimeMillis());
        String param = JSON.toJSONString(httpResult);
        httpParams.put("detail", param);
        post(tag, url, param, listener);
    }

    public static <T> void postNoHeader(Object tag, String url, HashMap<String, String> map, ResponseListener<T> listener) {
        HttpParams httpParams = new HttpParams();
        httpParams.put(map);
        post(tag, url, httpParams, listener);
    }

    public static <T> void postDetail(Object tag, String url, HashMap<String, String> map, ResponseListener<T> listener) {
        HttpParams httpParams = new HttpParams();
        String param = JSON.toJSONString(map);
        httpParams.put("detail", param);
        post(tag, url, httpParams, listener);
    }

    private static <T> void post(Object tag, String url, String httpParams, ResponseListener<T> listener) {
        url = CacheDataSource.getBaseUrl() + url;
        OkGo.<T>post(url)
                .params("detail", httpParams)
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    private static <T> void post(Object tag, String url, HttpParams httpParams, ResponseListener<T> listener) {
        url = CacheDataSource.getBaseUrl() + url;
        OkGo.<T>post(url)
                .params(httpParams)
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    public static <T> void get(Object tag, String url, HttpParams httpParams, ResponseListener<T> listener) {
        url = CacheDataSource.getBaseUrl() + url;
        OkGo.<T>get(url)
                .params(httpParams)
                .converter(new ObjectConverter<>(listener))
                .adapt(new ObservableBody<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(tag, url, listener));
    }

    /**
     * 下载文件
     *
     * @param serverUri          文件服务器地址
     * @param targetDir          文件目标存储目录
     * @param myDownloadListener 下载状态监听
     */
    public static DownloadTask downloadFile(Object tag, String serverUri, String targetDir, MyDownloadListener myDownloadListener) {
        DownloadListener downloadListener = new DownloadListener(tag) {
            @Override
            public void onStart(Progress progress) {

            }

            @Override
            public void onProgress(Progress progress) {
                myDownloadListener.downloadProgress(serverUri, (int) (progress.fraction * 100));
            }

            @Override
            public void onError(Progress progress) {
                if (progress.exception instanceof OkGoException) {
                    OkDownload.getInstance().getTask(serverUri).restart();
                } else if (progress.exception instanceof StorageException) {
                    OkDownload.getInstance().getTask(serverUri).restart();
                } else {
                    myDownloadListener.downloadFailed(serverUri);
                    OkDownload.getInstance().removeTask(progress.tag);
                }
            }

            @Override
            public void onFinish(File file, Progress progress) {
                myDownloadListener.downloadSuccess(file, serverUri);
                OkDownload.getInstance().removeTask(progress.tag);
            }

            @Override
            public void onRemove(Progress progress) {

            }
        };

        DownloadTask downloadTask = OkDownload.getInstance().getTask(serverUri);

        if (downloadTask == null) {
            GetRequest<File> fileGetRequest = OkGo.get(serverUri);
            downloadTask = OkDownload.request(serverUri, fileGetRequest)
                    .folder(targetDir)
                    .register(downloadListener);
        } else {
            downloadTask.register(downloadListener);
        }

        register(tag, downloadTask);

        return downloadTask;
    }

    private static <T> Observer<T> getObserver(Object tag, String url, ResponseListener<T> listener) {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                subscribe(tag, d);
            }

            @Override
            public void onNext(T t) {
                if (listener != null) {
                    listener.onSuccess(t);
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("接口调用失败", "onError:::" + url + ":::" + e.toString());
                if (listener != null) {
                    listener.onFailed("0", e.getMessage(), "");
                }
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private static void subscribe(Object tag, Disposable d) {
        if (tag != null) {
            CompositeDisposable subscription;
            if ((subscription = REQUEST_MAP.get(tag)) == null) {
                subscription = new CompositeDisposable();
                REQUEST_MAP.put(tag, subscription);
            }
            subscription.add(d);
        }
    }

    public static void unSubscribe(Object tag) {
        if (REQUEST_MAP.containsKey(tag)) {
            REQUEST_MAP.get(tag).dispose();
            REQUEST_MAP.remove(tag);
        }
    }

    private static void register(Object tag, Runnable task) {
        ArrayList<Runnable> tasks = TASKS_MAP.get(tag);
        if (tasks == null) {
            tasks = new ArrayList<>();
            TASKS_MAP.put(tag, tasks);
        }
        tasks.add(task);
    }

    public static void unRegister(Object tag) {
        ArrayList<Runnable> tasks = TASKS_MAP.get(tag);
        if (tasks != null) {
            for (Runnable task : tasks) {
                if (task instanceof DownloadTask) {
                    ((DownloadTask) task).listeners.remove(tag);
                }
                if (task instanceof UploadTask) {
                    ((UploadTask) task).listeners.remove(tag);
                }
            }
            TASKS_MAP.remove(tag);
        }
    }

    @SuppressLint("BadHostnameVerifier")
    private static class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
