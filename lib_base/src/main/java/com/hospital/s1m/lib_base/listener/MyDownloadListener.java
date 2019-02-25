package com.hospital.s1m.lib_base.listener;

import java.io.File;

/**
 * Created by 10295 on 2018/1/9.
 * 上传文件事件监听
 */

public interface MyDownloadListener {
    void downloadProgress(String serverUri, int progress);

    void downloadSuccess(File file, String serverUri);

    void downloadFailed(String serverUri);
}
