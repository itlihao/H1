package registration.zhiyihealth.com.lib_ime.listener;

import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;

/**
 * Created by Lihao on 2019-1-2.
 * Email heaolihao@163.com
 */
public interface PinYinConnector {
    void onShowCandiateView(PinYinManager.DecodingInfo info, PinYinManager.ImeState imeState, boolean showComposingView);

    void onResetState(PinYinManager.DecodingInfo info, PinYinManager.ImeState imeState, boolean showComposingView);

    void onHiddenCompose();
}
