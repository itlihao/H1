package registration.zhiyihealth.com.lib_ime.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.widget.EditText;

import com.android.inputmethod.pinyin.IPinyinDecoderService;
import com.android.inputmethod.pinyin.InputMode;
import com.android.inputmethod.pinyin.KeyMapDream;
import com.android.inputmethod.pinyin.NewEnglishInputProcessor;
import com.android.inputmethod.pinyin.PinyinDecoderService;
import com.android.inputmethod.pinyin.SoftKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import registration.zhiyihealth.com.lib_ime.listener.PinYinConnector;

/**
 * @author Lihao
 * @date 2018-12-29
 * Email heaolihao@163.com
 */
public class PinYinManager {
    private static final String TAG = PinYinManager.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static PinYinManager pinYinManager;

    private static final List<PinYinConnector> mObServerList = new ArrayList<>();

    private Context mContext;

    private EditText currentEdit;

    private int maxLength;

    private boolean isCanInput = true;

    /**
     * Connection used to bind the decoding service.  链接
     * 词库解码远程服务PinyinDecoderService 的监听器
     */
    private PinyinDecoderServiceConnection mPinyinDecoderServiceConnection;

    /**
     * Remote Pinyin-to-Hanzi decoding engine service.解码引擎远程服务
     */
    private IPinyinDecoderService mIPinyinDecoderService;

    /**
     * The current IME status. 当前的输入法状态
     */
    private ImeState mImeState = ImeState.STATE_IDLE;

    /**
     * 输入法状态变换器
     */
    private InputMode mInputMode;

    /**
     * The decoding information, include spelling(Pinyin) string, decoding
     * result, etc.词库解码操作对象
     */
    private DecodingInfo mDecInfo = new DecodingInfo();

    /**
     * For English input.  英文输入法按键处理器
     */
    private NewEnglishInputProcessor mImEn;

    private boolean candidatesIsShow = false;

    private int activeCandiatePos;

    public static PinYinManager getInstance() {
        if (pinYinManager == null) {
            pinYinManager = new PinYinManager();
        }
        return pinYinManager;
    }

    public void initialize(Context context) {
        mContext = context;

        mInputMode = new InputMode();
        mImEn = new NewEnglishInputProcessor();
        startPinyinDecoderService();
    }

    public void unInitialize() {
        // 解绑定词库解码远程服务PinyinDecoderService
        mContext.unbindService(mPinyinDecoderServiceConnection);
    }

    private EditText getCurrentEdit() {
        return currentEdit;
    }

    public void setCurrentEdit(EditText currentEdit, int maxLen) {
        this.currentEdit = currentEdit;
        this.maxLength = maxLen;
        isCanInput = true;
    }

    public boolean isCanInput() {
        return isCanInput;
    }

    public void addObserver(PinYinConnector observer) {
        synchronized (mObServerList) {
            Log.i(TAG, "Observer added:" + observer.getClass().getName());
            if (!mObServerList.contains(observer)) {
                mObServerList.add(observer);
            }
        }
    }

    void removeObserver(PinYinConnector observer) {
        synchronized (mObServerList) {
            Log.i(TAG, "Observer removed:" + observer.getClass().getName());
            mObServerList.remove(observer);
        }
    }

    public void setCandidatesState(boolean show) {
        candidatesIsShow = show;
    }


    /**
     * 绑定词库解码远程服务PinyinDecoderService
     */
    private void startPinyinDecoderService() {
        if (null == mIPinyinDecoderService) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(mContext, PinyinDecoderService.class);

            if (null == mPinyinDecoderServiceConnection) {
                mPinyinDecoderServiceConnection = new PinyinDecoderServiceConnection();
            }

            // Bind service
            mContext.bindService(serviceIntent, mPinyinDecoderServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Connection used for binding to the Pinyin decoding service.
     * 词库解码远程服务PinyinDecoderService 的监听器
     */
    public class PinyinDecoderServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIPinyinDecoderService = IPinyinDecoderService.Stub
                    .asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    public void switchLanguage(boolean isCN) {
        if (isCN) {
            mInputMode.switchChinese();
        } else {
            mInputMode.switchEnglish();
        }
    }

    public void switchUpper(boolean isUpper) {
        mInputMode.switchShift(isUpper);
    }
    /**
     * 响应软键盘按键的处理函数
     *
     * @param sKey
     */
    public void responseSoftKeyClick(SoftKey sKey) {
        if (null == sKey) {
            return;
        }

        EditText ic = getCurrentEdit();
        if (ic == null) {
            return;
        }

        int keyCode = sKey.getKeyCode();
        // 是系统的功能键，删除、空格、确定、导航CENTER
        if (sKey.isKeyCodeKey()) {
            // 功能键处理函数
            if (processFunctionKeys(keyCode, true)) {
                return;
            }
        }
        // 是用户定义的keycode
        if (sKey.isUserDefKey()) {
            // 通过我们定义的软键盘的按键，切换输入法模式。
//            mInputMode.switchModeForUserKey(keyCode);
            resetToIdleState(false);
        } else {
            // 是系统的keycode
            if (sKey.isKeyCodeKey()) {
                KeyEvent eDown = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                        keyCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD);
                KeyEvent eUp = new KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode,
                        0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD);

                onKeyDown(keyCode, eDown);
                onKeyUp(keyCode, eUp);
            } else if (sKey.isUniStrKey()) {
                // 是字符按键
                boolean kUsed = false;
                // 获取按键的字符
                String keyLabel = sKey.getKeyLabel();
                /*if (mInputMode.isChineseTextWithSkb()
                        && (ImeState.STATE_INPUT == mImeState || ImeState.STATE_COMPOSING == mImeState)) {
                    if (mDecInfo.length() > 0 && keyLabel.length() == 1
                            && keyLabel.charAt(0) == '\'') {
                        // 加入拼音分隔符，然后进行词库查询
                        processSurfaceChange('\'', 0);
                        kUsed = true;
                    }
                }
                if (!kUsed) {
                    if (ImeState.STATE_INPUT == mImeState) {
                        // 发送高亮候选词给EditText
                        commitResultText(mDecInfo.getCurrentFullSent(mCandidatesContainer.getActiveCandiatePos()));
                    } else if (ImeState.STATE_COMPOSING == mImeState) {
                        // 发送 拼音字符串（有可能存在选中的候选词） 给EditText
                        commitResultText(mDecInfo.getComposingStr());
                    }
                    // 发送 按键的字符 给EditText
                    commitResultText(keyLabel);
                    resetToIdleState(false);
                }*/
            }
        }
    }

    /**
     * 功能键处理函数
     */
    private boolean processFunctionKeys(int keyCode, boolean realAction) {
        //后退键的处理
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // TODO 返回键的处理
        }

        //中文相关输入是单独处理的，不在这边处理。
        if (mInputMode.isChineseText()) {
            return false;
        }

        // 候选词视图显示的时候
        if (candidatesIsShow && !mDecInfo.isCandidatesListEmpty()) {
            // 导航键 确定键
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (!realAction) {
                    return true;
                }
                // 选择当前高亮的候选词
                chooseCandidate(-1);
                return true;
            }

            // 导航键 向左
            /*if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (!realAction) {
                    return true;
                }
                // 高亮位置向上一个候选词移动或者移动到上一页的最后一个候选词的位置。
                mCandidatesContainer.activeCurseBackward();
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (!realAction) {
                    return true;
                }
                // 高亮位置向下一个候选词移动或者移动到下一页的第一个候选词的位置。
                mCandidatesContainer.activeCurseForward();
                return true;
            }

            // 向上翻页键
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (!realAction) {
                    return true;
                }
                // 到上一页候选词
                mCandidatesContainer.pageBackward(false, true);
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (!realAction) {
                    return true;
                }
                // 到下一页候选词
                mCandidatesContainer.pageForward(false, true);
                return true;
            }*/
            // 在预报状态下的删除键处理
            if (keyCode == KeyEvent.KEYCODE_DEL && ImeState.STATE_PREDICT == mImeState) {
                if (!realAction) {
                    return true;
                }
                resetToIdleState(false);
                return true;
            }
        }
        // 没有候选词显示的时候
        else {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (!realAction) {
                    return true;
                }
                EditText ic = getCurrentEdit();
                if (null != ic) {
                    StringBuilder sc = new StringBuilder(ic.getText().toString());
                    if (sc.length() >= 1) {
                        sc.deleteCharAt(sc.length() - 1);
                        ic.setText(sc.toString());
                        ic.setSelection(sc.length());
                    }
                    if (sc.length() < maxLength) {
                        isCanInput = true;
                    }
                }
                /*if (SIMULATE_KEY_DELETE) {
                    // 给EditText发送一个删除按键的按下和弹起事件。
                    simulateKeyEventDownUp(keyCode);
                } else {
                    // 发送删除一个字符的操作给EditText
                    getCurrentInputConnection().deleteSurroundingText(1, 0);
                }*/
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!realAction) {
                    return true;
                }
                // 发送Enter键给EditText
//                sendKeyChar('\n');
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_SPACE) {
                if (!realAction) {
                    return true;
                }
                // 发送' '字符给EditText
//                sendKeyChar(' ');
                return true;
            }
        }

        return false;
    }

    private void onKeyDown(int keyCode, KeyEvent event) {
        processKey(event, 0 != event.getRepeatCount());
    }

    private void onKeyUp(int keyCode, KeyEvent event) {
        processKey(event, true);
    }

    /**
     * 按键处理函数
     *
     * @param event
     * @param realAction
     * @return
     */
    private void processKey(KeyEvent event, boolean realAction) {
        if (ImeState.STATE_BYPASS == mImeState) {
            return;
        }

        int keyCode = event.getKeyCode();
        // SHIFT + SPACE 按键组合处理
        /*if (KeyEvent.KEYCODE_SPACE == keyCode && event.isShiftPressed()) {
            if (!realAction) {
                return true;
            }

            updateIcon(mInputModeSwitcher.switchLanguageWithHkb());
            resetToIdleState(false);
            // 清除alt shift sym 键按住的状态
            int allMetaState = KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON
                    | KeyEvent.META_ALT_RIGHT_ON | KeyEvent.META_SHIFT_ON
                    | KeyEvent.META_SHIFT_LEFT_ON
                    | KeyEvent.META_SHIFT_RIGHT_ON | KeyEvent.META_SYM_ON;
            getCurrentInputConnection().clearMetaKeyStates(allMetaState);
            return true;
        }*/

        // 如果是硬键盘英文输入状态，就忽略掉该按键，让默认的按键监听器去处理它。
        if (mInputMode.isEnglishWithHkb()) {
            return;
        }
        //功能键处理
        if (processFunctionKeys(keyCode, realAction)) {
            return;
        }

        int keyChar = 0;
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            keyChar = keyCode - KeyEvent.KEYCODE_A + 'a';
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            keyChar = keyCode - KeyEvent.KEYCODE_0 + '0';
        } else if (keyCode == KeyEvent.KEYCODE_COMMA) {
            keyChar = ',';
        } else if (keyCode == KeyEvent.KEYCODE_PERIOD) {
            keyChar = '.';
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            keyChar = ' ';
        } else if (keyCode == KeyEvent.KEYCODE_APOSTROPHE) {
            keyChar = '\'';
        }

        if (mInputMode.isEnglishWithSkb()) {
            // 英文软键盘处理
            mImEn.processKey(getCurrentEdit(), event, mInputMode.isEnglishUpperCaseWithSkb(), realAction);
        } else if (mInputMode.isChineseText()) {
            Log.w(TAG, "输入法模式, " + mImeState);
            // 中文输入法模式
            if (mImeState == ImeState.STATE_IDLE ||
                    mImeState == ImeState.STATE_APP_COMPLETION) {
                mImeState = ImeState.STATE_IDLE;
                processStateIdle(keyChar, keyCode, event, realAction);
            } else if (mImeState == ImeState.STATE_INPUT) {
                processStateInput(keyChar, keyCode, event, realAction);
            } else if (mImeState == ImeState.STATE_PREDICT) {
                processStatePredict(keyChar, keyCode, event, realAction);
            } else if (mImeState == ImeState.STATE_COMPOSING) {
                processStateEditComposing(keyChar, keyCode, event, realAction);
            }
        } else {
            // 符号处理
            if (0 != keyChar && realAction) {
                // 发送文本给EditText
                commitResultText(String.valueOf((char) keyChar));
            }
        }
    }

    /**
     * 当 mImeState == ImeState.STATE_IDLE 或ImeState.STATE_APP_COMPLETION 时的按键处理函数
     */
    private void processStateIdle(int keyChar, int keyCode, KeyEvent event, boolean realAction) {
        // In this status, when user presses keys in [a..z], the status will change to input state.
        if (keyChar >= 'a' && keyChar <= 'z' && !event.isAltPressed()) {
            if (!realAction) {
                return;
            }
            mDecInfo.addSplChar((char) keyChar, true);
            // 对输入的拼音进行查询
            chooseAndUpdate(-1);

        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (!realAction) {
                return;
            }
            // TODO 删除键处理
            EditText ic = getCurrentEdit();
            if (null != ic) {
                StringBuilder sc = new StringBuilder(ic.getText().toString());
                if (sc.length() >= 1) {
                    sc.deleteCharAt(sc.length() - 1);
                    ic.setText(sc.toString());
                    ic.setSelection(sc.length());
                }
                if (sc.length() < maxLength) {
                    isCanInput = true;
                }
            }
            /*if (SIMULATE_KEY_DELETE) {
                // 模拟删除键发送给 EditText
                simulateKeyEventDownUp(keyCode);
            } else {
                // 发送删除一个字符的操作给 EditText
                getCurrentInputConnection().deleteSurroundingText(1, 0);
            }*/

        } else if (event.isAltPressed()) {
            // 获取中文全角字符
            char fullwidthChar = KeyMapDream.getChineseLabel(keyCode);
            if (0 != fullwidthChar) {
                if (realAction) {
                    String result = String.valueOf(fullwidthChar);
                    commitResultText(result);
                }
            } else {
            }
        } else if (keyChar != 0 && keyChar != '\t') {
            if (realAction) {
                if (keyChar == ',' || keyChar == '.') {
                    // 发送 '\uff0c' 或者 '\u3002' 给EditText
                    inputCommaPeriod("", keyChar, false, ImeState.STATE_IDLE);
                } else {
                    if (0 != keyChar) {
                        String result = String.valueOf((char) keyChar);
                        commitResultText(result);
                    }
                }
            }
        }
    }

    /**
     * 当 mImeState == ImeState.STATE_INPUT 时的按键处理函数
     *
     * @param keyChar
     * @param keyCode
     * @param event
     * @param realAction
     * @return
     */
    private void processStateInput(int keyChar, int keyCode, KeyEvent event, boolean realAction) {
        //如果 ALT 被按住
        /*if (event.isAltPressed()) {
            if ('\'' != event.getUnicodeChar(event.getMetaState())) {
                if (realAction) {
                    // 获取中文全角字符
                    char fullwidthChar = KeyMapDream.getChineseLabel(keyCode);
                    if (0 != fullwidthChar) {
                        // 发送高亮的候选词 + 中文全角字符 给 EditView
                        commitResultText(mDecInfo.getCurrentFullSent(mCandidatesContainer
                                .getActiveCandiatePos()) + String.valueOf(fullwidthChar));
                        resetToIdleState(false);
                    }
                }
                return true;
            } else {
                keyChar = '\'';
            }
        }*/

        if (keyChar >= 'a' && keyChar <= 'z' || keyChar == '\''
                && !mDecInfo.charBeforeCursorIsSeparator() || keyCode == KeyEvent.KEYCODE_DEL) {
            if (!realAction) {
                return;
            }
            // 添加输入的拼音，然后进行词库查询，或者删除输入的拼音指定的字符或字符串，然后进行词库查询。
            processSurfaceChange(keyChar, keyCode);
        } else if (keyChar == ',' || keyChar == '.') {
            if (!realAction) {
            }
            // 发送 '\uff0c' 或者 '\u3002' 给EditText
//            inputCommaPeriod(mDecInfo.getCurrentFullSent(mCandidatesContainer
//                    .getActiveCandiatePos()), keyChar, true, ImeState.STATE_IDLE);
        }
        // 上下左右导航键
        /*else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (!realAction) {
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                // 高亮位置向上一个候选词移动或者移动到上一页的最后一个候选词的位置。
                mCandidatesContainer.activeCurseBackward();
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                // 高亮位置向下一个候选词移动或者移动到下一页的第一个候选词的位置。
                mCandidatesContainer.activeCurseForward();
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                // If it has been the first page, a up key will shift
                // the state to edit composing string.
                // 到上一页候选词
                if (!mCandidatesContainer.pageBackward(false, true)) {
                    mCandidatesContainer.enableActiveHighlight(false);
                    changeToStateComposing(true);
                    updateComposingText(true);
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                // 到下一页候选词
                mCandidatesContainer.pageForward(false, true);
            }
            return true;
        }*/

        else if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            if (!realAction) {
                return;
            }
            String result = String.valueOf((char) keyChar);
            commitResultText(result);
        }
        /*else if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            if (!realAction) {
                return true;
            }

            int activePos = keyCode - KeyEvent.KEYCODE_1;
            int currentPage = mCandidatesContainer.getCurrentPage();
            if (activePos < mDecInfo.getCurrentPageSize(currentPage)) {
                activePos = activePos + mDecInfo.getCurrentPageStart(currentPage);
                if (activePos >= 0) {
                    // 选择候选词，并根据条件是否进行下一步的预报。
                    chooseAndUpdate(activePos);
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (!realAction) {
                return true;
            }
            if (mInputMode.isEnterNoramlState()) {
                // 把输入的拼音字符串发送给EditText
                commitResultText(mDecInfo.getOrigianlSplStr().toString());
                resetToIdleState(false);
            } else {
                // 把高亮的候选词发送给EditText
                commitResultText(mDecInfo
                        .getCurrentFullSent(mCandidatesContainer.getActiveCandiatePos()));
                sendKeyChar('\n');
                resetToIdleState(false);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (!realAction) {
                return true;
            }
            // 选择高亮的候选词
            chooseCandidate(-1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!realAction) {
                return true;
            }
            resetToIdleState(false);
            // 关闭输入法
            requestHideSelf(0);
            return true;
        }*/
    }

    /**
     * 当 mImeState == ImeState.STATE_PREDICT 时的按键处理函数
     *
     * @param keyChar
     * @param keyCode
     * @param event
     * @param realAction
     * @return
     */
    private void processStatePredict(int keyChar, int keyCode, KeyEvent event, boolean realAction) {
        if (!realAction) {
            return;
        }

        // 按住Alt键
        /*if (event.isAltPressed()) {
            // 获取中文全角字符
            char fullwidth_char = KeyMapDream.getChineseLabel(keyCode);
            if (0 != fullwidth_char) {
                // 发送高亮的候选词 + 中文全角字符 给 EditView
                commitResultText(mDecInfo.getCandidate(mCandidatesContainer
                        .getActiveCandiatePos()) + String.valueOf(fullwidth_char));
                resetToIdleState(false);
            }
            return true;
        }*/

        // In this status, when user presses keys in [a..z], the status will change to input state.
        if (keyChar >= 'a' && keyChar <= 'z') {
            changeToStateInput(true);
            // 加一个字符进输入的拼音字符串中
            mDecInfo.addSplChar((char) keyChar, true);
            // 对输入的拼音进行查询。
            chooseAndUpdate(-1);
        } else if (keyChar == ',' || keyChar == '.') {
            // 发送 '\uff0c' 或者 '\u3002' 给EditText
            inputCommaPeriod("", keyChar, true, ImeState.STATE_IDLE);
        } /*else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                // 高亮位置向上一个候选词移动或者移动到上一页的最后一个候选词的位置。
                mCandidatesContainer.activeCurseBackward();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                // 高亮位置向下一个候选词移动或者移动到下一页的第一个候选词的位置。
                mCandidatesContainer.activeCurseForward();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                // 到上一页候选词
                mCandidatesContainer.pageBackward(false, true);
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                // 到下一页候选词
                mCandidatesContainer.pageForward(false, true);
            }
        }*/ else if (keyCode == KeyEvent.KEYCODE_DEL) {
            resetToIdleState(false);
        } /*else if (keyCode == KeyEvent.KEYCODE_BACK) {
            resetToIdleState(false);
            // 关闭输入法
            requestHideSelf(0);
        } else if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            int activePos = keyCode - KeyEvent.KEYCODE_1;
            int currentPage = mCandidatesContainer.getCurrentPage();
            if (activePos < mDecInfo.getCurrentPageSize(currentPage)) {
                activePos = activePos + mDecInfo.getCurrentPageStart(currentPage);
                if (activePos >= 0) {
                    // 选择候选词
                    chooseAndUpdate(activePos);
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // 发生ENTER键给EditText
            sendKeyChar('\n');
            resetToIdleState(false);
        }*/ else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
            // 选择候选词
            chooseCandidate(-1);
        } else if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            String result = String.valueOf((char) keyChar);
            commitResultText(result);
        }
    }

    /**
     * 当 mImeState == ImeState.STATE_COMPOSING 时的按键处理函数
     *
     * @param keyChar
     * @param keyCode
     * @param event
     * @param realAction
     * @return
     */
    private void processStateEditComposing(int keyChar, int keyCode, KeyEvent event, boolean realAction) {
        if (!realAction) {
            return;
        }
        // 获取输入的音字符串的状态
        /*ComposingView.ComposingStatus cmpsvStatus = mComposingView.getComposingStatus();

        // If ALT key is pressed, input alternative key. But if the
        // alternative key is quote key, it will be used for input a splitter
        // in Pinyin string.
        // 按住 ALT 键
        if (event.isAltPressed()) {
            if ('\'' != event.getUnicodeChar(event.getMetaState())) {
                // 获取中文全角字符
                char fullwidthChar = KeyMapDream.getChineseLabel(keyCode);
                if (0 != fullwidthChar) {
                    String retStr;
                    if (ComposingView.ComposingStatus.SHOW_STRING_LOWERCASE == cmpsvStatus) {
                        // 获取原始的输入拼音的字符
                        retStr = mDecInfo.getOrigianlSplStr().toString();
                    } else {
                        // 获取组合的输入拼音的字符（有可能存在选中的候选词）
                        retStr = mDecInfo.getComposingStr();
                    }
                    // 发送文本给EditText
                    commitResultText(retStr + String.valueOf(fullwidthChar));
                    resetToIdleState(false);
                }
                return true;
            } else {
                keyChar = '\'';
            }
        }*/

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (!mDecInfo.selectionFinished()) {
                changeToStateInput(true);
            }
        } /*else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // 移动候选词的光标
            mComposingView.moveCursor(keyCode);
        } else if ((keyCode == KeyEvent.KEYCODE_ENTER && mInputMode.isEnterNoramlState())
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (ComposingView.ComposingStatus.SHOW_STRING_LOWERCASE == cmpsvStatus) {
                // 获取原始的输入拼音的字符
                String str = mDecInfo.getOrigianlSplStr().toString();
                if (!tryInputRawUnicode(str)) {
                    // 发送文本给EditText
                    commitResultText(str);
                }
            } else if (ComposingView.ComposingStatus.EDIT_PINYIN == cmpsvStatus) {
                String str = mDecInfo.getComposingStr();
                if (!tryInputRawUnicode(str)) {
                    commitResultText(str);
                }
            } else {
                // 发生 组合的输入拼音的字符（有可能存在选中的候选词） 给 EditText
                commitResultText(mDecInfo.getComposingStr());
            }
            resetToIdleState(false);
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && !mInputMode.isEnterNoramlState()) {
            String retStr;
            if (!mDecInfo.isCandidatesListEmpty()) {
                // 获取当前高亮的候选词
                retStr = mDecInfo.getCurrentFullSent(mCandidatesContainer.getActiveCandiatePos());
            } else {
                // 获取组合的输入拼音的字符（有可能存在选中的候选词）
                retStr = mDecInfo.getComposingStr();
            }
            commitResultText(retStr);
            sendKeyChar('\n');
            resetToIdleState(false);
        }*/ else if (keyCode == KeyEvent.KEYCODE_BACK) {
            resetToIdleState(false);
            //TODO 关闭输入法
//            requestHideSelf(0);
        } else {
            // 添加输入的拼音，然后进行词库查询，或者删除输入的拼音指定的字符或字符串，然后进行词库查询。
            processSurfaceChange(keyChar, keyCode);
        }
    }

    /**
     * 添加输入的拼音，然后进行词库查询，或者删除输入的拼音指定的字符或字符串，然后进行词库查询。
     *
     * @param keyChar
     * @param keyCode
     * @return
     */
    private boolean processSurfaceChange(int keyChar, int keyCode) {
        if (mDecInfo.isSplStrFull() && KeyEvent.KEYCODE_DEL != keyCode) {
            return true;
        }

        if ((keyChar >= 'a' && keyChar <= 'z') || (keyChar == '\'' && !mDecInfo.charBeforeCursorIsSeparator())
                || (((keyChar >= '0' && keyChar <= '9') || keyChar == ' ') && ImeState.STATE_COMPOSING == mImeState)) {
            mDecInfo.addSplChar((char) keyChar, false);
            // TODO  查询
            chooseAndUpdate(-1);
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            mDecInfo.prepareDeleteBeforeCursor();
            chooseAndUpdate(-1);
        }
        return true;
    }

    /**
     * 发送 '\uff0c' 或者 '\u3002' 给EditText
     *
     * @param preEdit
     * @param keyChar
     * @param dismissCandWindow 是否重置候选词窗口
     * @param nextState         mImeState的下一个状态
     */
    private void inputCommaPeriod(String preEdit, int keyChar, boolean dismissCandWindow, ImeState nextState) {
        if (keyChar == ',') {
            preEdit += '\uff0c';
        } else if (keyChar == '.') {
            preEdit += '\u3002';
        } else {
            return;
        }
        commitResultText(preEdit);
        if (dismissCandWindow) {
//            resetCandidateWindow();
        }
        mImeState = nextState;
    }

    /**
     * 选择候选词，并根据条件是否进行下一步的预报。
     *
     * @param candId 如果candId小于0 ，就对输入的拼音进行查询。
     */
    private void chooseAndUpdate(int candId) {
        // 不是中文输入法状态
        if (!mInputMode.isChineseText()) {
            String choice = mDecInfo.getCandidate(candId);
            if (null != choice) {
                commitResultText(choice);
            }
            resetToIdleState(false);
            return;
        }

        if (ImeState.STATE_PREDICT != mImeState) {
            // Get result candidate list, if choice_id < 0, do a new decoding.
            // If choice_id >=0, select the candidate, and get the new candidate list.
            mDecInfo.chooseDecodingCandidate(candId);
        } else {
            // Choose a prediction item.
            mDecInfo.choosePredictChoice(candId);
        }

        if (mDecInfo.getComposingStr().length() > 0) {
            String resultStr;
            // 获取选择了的候选词
            resultStr = mDecInfo.getComposingStrActivePart();
            // choiceId >= 0 means user finishes a choice selection.
            if (candId >= 0 && mDecInfo.canDoPrediction()) {
                // 发生选择了的候选词给EditText
                commitResultText(resultStr);
                // 设置输入法状态为预报
                mImeState = ImeState.STATE_PREDICT;
                // TODO 这一步是做什么？
                /*if (null != mSkbContainer && mSkbContainer.isShown()) {
                    mSkbContainer.toggleCandidateMode(false);
                }*/

                // 获取预报的候选词列表
                /*if (Settings.getPrediction()) {
                 if (null != ic) {
                        CharSequence cs = ic.getTextBeforeCursor(3, 0);
                        if (null != cs) {
                            mDecInfo.preparePredicts(cs);
                        }
                    }
                    EditText ic = getCurrentEdit();
                    if (null != ic) {
                        mDecInfo.preparePredicts(ic.getText().toString());
                    }
                } else {
                    mDecInfo.resetCandidates();
                }*/
                EditText ic = getCurrentEdit();
                if (null != ic) {
                    mDecInfo.preparePredicts(ic.getText().toString());
                }

                if (mDecInfo.mCandidatesList.size() > 0) {
                    showCandidateWindow(false);
                } else {
                    resetToIdleState(false);
                }
            } else {
                if (ImeState.STATE_IDLE == mImeState) {
                    if (mDecInfo.getSplStrDecodedLen() == 0) {
                        changeToStateComposing(true);
                    } else {
                        changeToStateInput(true);
                    }
                } else {
                    if (mDecInfo.selectionFinished()) {
                        changeToStateComposing(true);
                    }
                }
                showCandidateWindow(true);
            }
        } else {
            resetToIdleState(false);
        }
    }

    /**
     * 选择候选词后的处理函数。在ChoiceNotifier中实现CandidateViewListener监听器的onClickChoice（）中调用
     *
     * @param activeCandNo
     */
    public void onChoiceTouched(int activeCandNo) {
        if (mImeState == ImeState.STATE_COMPOSING) {
            changeToStateInput(true);
        } else if (mImeState == ImeState.STATE_INPUT
                || mImeState == ImeState.STATE_PREDICT) {
            // 选择候选词
            chooseCandidate(activeCandNo);
        } else if (mImeState == ImeState.STATE_APP_COMPLETION) {
            if (null != mDecInfo.mAppCompletions && activeCandNo >= 0 &&
                    activeCandNo < mDecInfo.mAppCompletions.length) {
                CompletionInfo ci = mDecInfo.mAppCompletions[activeCandNo];
                if (null != ci) {
                    // 发送从APP中获取的候选词给EditText
                    // TODO 更新EditText
                    /*InputConnection ic = getCurrentInputConnection();
                    ic.commitCompletion(ci);*/
                    commitResultText(ci.toString());
                }
            }
            resetToIdleState(false);
        }
    }

    /**
     * 选择候选词
     *
     * @param activeCandNo 如果小于0，就选择当前高亮的候选词。
     */
    private void chooseCandidate(int activeCandNo) {
        // activeCandNo < 0 是功能键选择的
        /*if (activeCandNo < 0) {
            activeCandNo = mCandidatesContainer.getActiveCandiatePos();
        }*/
        // activeCandNo > 0 是候选词列表选择的
        if (activeCandNo >= 0) {
            chooseAndUpdate(activeCandNo);
        }
    }

    /**
     * 发送字符串给编辑框
     */
    private void commitResultText(String resultText) {
        // TODO 发送字符串给编辑框
        EditText ic = getCurrentEdit();
        if (null != ic) {
            StringBuffer sc = new StringBuffer(ic.getText().toString());

            if (sc.length() < maxLength) {
                if (sc.length() + resultText.length() <= maxLength) {
                    sc.append(resultText);
                } else {
                    sc.append(resultText.substring(0, maxLength - sc.length()));
                }
            }

            ic.setText(sc);
            ic.setSelection(sc.length());
            if (sc.length() == maxLength) {
                isCanInput = false;
            }
        }

        for (PinYinConnector obj : mObServerList) {
            obj.onHiddenCompose();
        }
    }

    /**
     * 重置到空闲状态
     */
    private void resetToIdleState(boolean resetInlineText) {
        if (ImeState.STATE_IDLE == mImeState) {
            return;
        }

        mImeState = ImeState.STATE_IDLE;
        mDecInfo.reset();

        if (resetInlineText) {
            commitResultText("");
        }

        for (PinYinConnector obj : mObServerList) {
            obj.onResetState(mDecInfo, mImeState, resetInlineText);
        }
        mDecInfo.resetCandidates();
//        resetCandidateWindow();
    }

    /**
     * 显示候选词视图
     *
     * @param showComposingView 是否显示输入的拼音View
     */
    private void showCandidateWindow(boolean showComposingView) {
        for (PinYinConnector obj : mObServerList) {
            obj.onShowCandiateView(mDecInfo, mImeState, showComposingView);
        }

        /*if (mEnvironment.needDebug()) {
            Log.d(TAG, "Candidates window is shown. Parent = " + mCandidatesContainer);
        }*/
    }

    /**
     * 输入法状态
     */
    public enum ImeState {
        STATE_BYPASS,
        STATE_IDLE,
        STATE_INPUT,
        STATE_COMPOSING,
        STATE_PREDICT,
        STATE_APP_COMPLETION
    }

    /**
     * 设置输入法状态为 mImeState = ImeState.STATE_COMPOSING;
     *
     * @param updateUi 是否更新UI
     */
    private void changeToStateComposing(boolean updateUi) {
        mImeState = ImeState.STATE_COMPOSING;
        if (!updateUi) {
            return;
        }
    }

    /**
     * 设置输入法状态为 mImeState = ImeState.STATE_INPUT;
     *
     * @param updateUi 是否更新UI
     */
    private void changeToStateInput(boolean updateUi) {
        mImeState = ImeState.STATE_INPUT;
        if (!updateUi) {
            return;
        }
//        showCandidateWindow(true);
    }

    public void setCandidatesList(List<String> list) {
        /*if (mDecInfo != null) {
            mDecInfo.mCandidatesList.clear();
            mDecInfo.mCandidatesList.addAll(list);

            mDecInfo.mPageStart.clear();
            mDecInfo.mPageStart.add(0);
            mDecInfo.mCnToPage.clear();
            mDecInfo.mCnToPage.add(0);

            mDecInfo.mTotalChoicesNum = 0;

            showCandidateWindow(false);
        }*/
        /*if (mDecInfo != null) {
            mDecInfo.mCandidatesList.addAll(list);
            showCandidateWindow(false);
        }*/
    }

    /**
     * 词库解码操作对象
     */
    public class DecodingInfo {
        /**
         * Maximum length of the Pinyin string最大的字符串的长度，其实只有27，因为最后一位为0，是mPyBuf[]的长度
         */
        private static final int PY_STRING_MAX = 28;

        /**
         * Maximum number of candidates to display in one page.一页显示候选词的最大个数
         */
        private static final int MAX_PAGE_SIZE_DISPLAY = 15;

        /**
         * Spelling (Pinyin) string.拼音字符串
         */
        private StringBuffer mSurface;

        /**
         * Byte buffer used as the Pinyin string parameter for native function
         * call.字符缓冲区作为拼音字符串参数给本地函数调用，它的长度为PY_STRING_MAX，最后一位为0
         */
        private byte mPyBuf[];

        /**
         * The length of surface string successfully decoded by engine.成功解码的字符串长度
         */
        private int mSurfaceDecodedLen;

        /**
         * Composing string.拼音字符串
         */
        private String mComposingStr;

        /**
         * Length of the active composing string.活动的拼音字符串长度
         */
        private int mActiveCmpsLen;

        /**
         * Composing string for display, it is copied from mComposingStr, and
         * add spaces between spellings.显示的拼音字符串，是从mComposingStr复制过来的，并且在拼写之间加上了空格。
         **/
        private String mComposingStrDisplay;

        /**
         * Length of the active composing string for display.显示的拼音字符串的长度
         */
        private int mActiveCmpsDisplayLen;

        /**
         * The first full sentence choice.第一个完整句子，第一个候选词。
         */
        private String mFullSent;

        /**
         * Number of characters which have been fixed.固定的字符的数量
         */
        private int mFixedLen;

        /**
         * If this flag is true, selection is finished.是否选择完成了？
         */
        private boolean mFinishSelection;

        /**
         * The starting position for each spelling. The first one is the number
         * of the real starting position elements.每个拼写的开始位置，猜测：第一个元素是拼写的总数量？
         */
        private int mSplStart[];

        /**
         * Editing cursor in mSurface.光标的位置
         */
        private int mCursorPos;

        /**
         * The complication information suggested by application.应用的并发建议信息
         */
        private CompletionInfo[] mAppCompletions;

        /**
         * The total number of choices for display. The list may only contains
         * the first part. If user tries to navigate to next page which is not
         * in the result list, we need to get these items.显示的可选择的总数
         **/
        int mTotalChoicesNum;

        /**
         * Candidate list. The first one is the full-sentence candidate.候选词列表
         */
        public List<String> mCandidatesList = new Vector<String>();

        /**
         * Element i stores the starting position of page i. 页的开始位置
         */
        public Vector<Integer> mPageStart = new Vector<Integer>();

        /**
         * Element i stores the number of characters to page i.
         * 每一页的数量
         */
        public Vector<Integer> mCnToPage = new Vector<Integer>();

        /**
         * The position to delete in Pinyin string. If it is less than 0, IME
         * will do an incremental search, otherwise IME will do a deletion
         * operation. if {@link #mIsPosInSpl} is true, IME will delete the whole
         * string for mPosDelSpl-th spelling, otherwise it will only delete
         * mPosDelSpl-th character in the Pinyin string.在拼音字符串中的删除位置
         */
        int mPosDelSpl = -1;

        /**
         * If {@link #mPosDelSpl} is big than or equal to 0, this member is used
         * to indicate that whether the postion is counted in spelling id or
         * character. 如果 mPosDelSpl 大于等于 0，那么这个参数就用于表明是否是 拼写的id 或者 字符。
         */
        boolean mIsPosInSpl;

        /**
         *
         */
        DecodingInfo() {
            mSurface = new StringBuffer();
            mSurfaceDecodedLen = 0;
        }

        /**
         * 重置
         */
        public void reset() {
            mSurface.delete(0, mSurface.length());
            mSurfaceDecodedLen = 0;
            mCursorPos = 0;
            mFullSent = "";
            mFixedLen = 0;
            mFinishSelection = false;
            mComposingStr = "";
            mComposingStrDisplay = "";
            mActiveCmpsLen = 0;
            mActiveCmpsDisplayLen = 0;

            resetCandidates();
        }

        /**
         * 候选词列表是否为空
         *
         * @return
         */
        public boolean isCandidatesListEmpty() {
            return mCandidatesList.size() == 0;
        }

        /**
         * 拼写的字符串是否已满
         *
         * @return
         */
        public boolean isSplStrFull() {
            return mSurface.length() >= PY_STRING_MAX - 1;
        }

        /**
         * 增加拼写字符
         *
         * @param ch
         * @param reset 拼写字符是否重置
         */
        public void addSplChar(char ch, boolean reset) {
            if (reset) {
                mSurface.delete(0, mSurface.length());
                mSurfaceDecodedLen = 0;
                mCursorPos = 0;
                try {
                    mIPinyinDecoderService.imResetSearch();
                    Log.w(TAG, "查询");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mSurface.insert(mCursorPos, ch);
            mCursorPos++;
        }

        // Prepare to delete before cursor. We may delete a spelling char if
        // the cursor is in the range of unfixed part, delete a whole spelling
        // if the cursor in inside the range of the fixed part.
        // This function only marks the position used to delete.

        /**
         * 删除前的准备。该函数只是标记要删除的位置。
         */
        public void prepareDeleteBeforeCursor() {
            if (mCursorPos > 0) {
                int pos;
                for (pos = 0; pos < mFixedLen; pos++) {
                    if (mSplStart[pos + 2] >= mCursorPos
                            && mSplStart[pos + 1] < mCursorPos) {
                        mPosDelSpl = pos;
                        mCursorPos = mSplStart[pos + 1];
                        mIsPosInSpl = true;
                        break;
                    }
                }
                if (mPosDelSpl < 0) {
                    mPosDelSpl = mCursorPos - 1;
                    mCursorPos--;
                    mIsPosInSpl = false;
                }
            }
        }

        /**
         * 获取拼音字符串长度
         *
         * @return
         */
        public int length() {
            return mSurface.length();
        }

        /**
         * 获得拼音字符串中指定位置的字符
         *
         * @param index
         * @return
         */
        char charAt(int index) {
            return mSurface.charAt(index);
        }

        /**
         * 获得拼音字符串
         *
         * @return
         */
        public StringBuffer getOrigianlSplStr() {
            return mSurface;
        }

        /**
         * 获得成功解码的字符串长度
         *
         * @return
         */
        int getSplStrDecodedLen() {
            return mSurfaceDecodedLen;
        }

        /**
         * 获得每个拼写字符串的开始位置
         *
         * @return
         */
        public int[] getSplStart() {
            return mSplStart;
        }

        /**
         * 获取拼音字符串，有可能存在选中的候选词
         *
         * @return
         */
        String getComposingStr() {
            return mComposingStr;
        }

        /**
         * 获取活动的拼音字符串，就是选择了的候选词。
         *
         * @return
         */
        @SuppressLint("Assert")
        String getComposingStrActivePart() {
            assert (mActiveCmpsLen <= mComposingStr.length());
            return mComposingStr.substring(0, mActiveCmpsLen);
        }

        /**
         * 获得活动的拼音字符串长度
         *
         * @return
         */
        public int getActiveCmpsLen() {
            return mActiveCmpsLen;
        }

        /**
         * 获取显示的拼音字符串
         *
         * @return
         */
        public String getComposingStrForDisplay() {
            return mComposingStrDisplay;
        }

        /**
         * 显示的拼音字符串的长度
         *
         * @return
         */
        public int getActiveCmpsDisplayLen() {
            return mActiveCmpsDisplayLen;
        }

        /**
         * 第一个完整句子
         *
         * @return
         */
        public String getFullSent() {
            return mFullSent;
        }

        /**
         * 获取当前完整句子
         *
         * @param activeCandPos
         * @return
         */
        public String getCurrentFullSent(int activeCandPos) {
            try {
                String retStr = mFullSent.substring(0, mFixedLen);
                retStr += mCandidatesList.get(activeCandPos);
                return retStr;
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * 重置候选词列表
         */
        void resetCandidates() {
            mCandidatesList.clear();
            mTotalChoicesNum = 0;

            mPageStart.clear();
            mPageStart.add(0);
            mCnToPage.clear();
            mCnToPage.add(0);
        }

        /**
         * 候选词来自app，判断输入法状态 mImeState == ImeState.STATE_APP_COMPLETION。
         *
         * @return
         */
        public boolean candidatesFromApp() {
            return ImeState.STATE_APP_COMPLETION == mImeState;
        }

        /**
         * 判断 mComposingStr.length() == mFixedLen ？
         *
         * @return
         */
        boolean canDoPrediction() {
            return mComposingStr.length() == mFixedLen;
        }

        /**
         * 选择是否完成
         *
         * @return
         */
        boolean selectionFinished() {
            return mFinishSelection;
        }

        //如果candId〉0，就选择一个候选词，并且重新获取一个候选词列表，选择的候选词存放在mComposingStr中，通过mDecInfo.
        //getComposingStrActivePart()取出来。如果candId小于0 ，就对输入的拼音进行查询。
        private void chooseDecodingCandidate(int candId) {
            if (mImeState != ImeState.STATE_PREDICT) {
                resetCandidates();
                int totalChoicesNum = 0;
                try {
                    if (candId < 0) {
                        if (length() > 0) {
                            if (mPyBuf == null) {
                                mPyBuf = new byte[PY_STRING_MAX];
                            }
                            for (int i = 0; i < length(); i++) {
                                mPyBuf[i] = (byte) charAt(i);
                            }
                            mPyBuf[length()] = 0;

                            if (mPosDelSpl < 0) {
                                totalChoicesNum = mIPinyinDecoderService.imSearch(mPyBuf, length());
                            } else {
                                boolean clearFixedThisStep = true;
                                if (ImeState.STATE_COMPOSING == mImeState) {
                                    clearFixedThisStep = false;
                                }
                                totalChoicesNum = mIPinyinDecoderService
                                        .imDelSearch(mPosDelSpl, mIsPosInSpl, clearFixedThisStep);
                                mPosDelSpl = -1;
                            }
                        }
                    } else {
                        totalChoicesNum = mIPinyinDecoderService.imChoose(candId);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                updateDecInfoForSearch(totalChoicesNum);
                // mCandidatesList
                StringBuilder str = new StringBuilder();
                if (mCandidatesList.size() > 0) {
                    for (int i = 0; i < mCandidatesList.size(); i++) {
                        str.append(mCandidatesList.get(i));
                        str.append("，");
                    }
                }
                Log.w(TAG, "字符串: " + mSurface + " ,查询结果: " + str);
            }
        }

        /**
         * 更新查询词库后的信息
         *
         * @param totalChoicesNum
         */
        @SuppressLint("Assert")
        private void updateDecInfoForSearch(int totalChoicesNum) {
            mTotalChoicesNum = totalChoicesNum;
            if (mTotalChoicesNum < 0) {
                mTotalChoicesNum = 0;
                return;
            }

            try {
                String pyStr;

                mSplStart = mIPinyinDecoderService.imGetSplStart();
                pyStr = mIPinyinDecoderService.imGetPyStr(false);
                mSurfaceDecodedLen = mIPinyinDecoderService.imGetPyStrLen(true);
                assert (mSurfaceDecodedLen <= pyStr.length());

                mFullSent = mIPinyinDecoderService.imGetChoice(0);
                mFixedLen = mIPinyinDecoderService.imGetFixedLen();

                // Update the surface string to the one kept by engine.
                mSurface.replace(0, mSurface.length(), pyStr);

                if (mCursorPos > mSurface.length()) {
                    mCursorPos = mSurface.length();
                }
                mComposingStr = mFullSent.substring(0, mFixedLen)
                        + mSurface.substring(mSplStart[mFixedLen + 1]);

                mActiveCmpsLen = mComposingStr.length();
                if (mSurfaceDecodedLen > 0) {
                    mActiveCmpsLen = mActiveCmpsLen
                            - (mSurface.length() - mSurfaceDecodedLen);
                }

                // Prepare the display string.
                if (0 == mSurfaceDecodedLen) {
                    mComposingStrDisplay = mComposingStr;
                    mActiveCmpsDisplayLen = mComposingStr.length();
                } else {
                    mComposingStrDisplay = mFullSent.substring(0, mFixedLen);
                    for (int pos = mFixedLen + 1; pos < mSplStart.length - 1; pos++) {
                        mComposingStrDisplay += mSurface.substring(
                                mSplStart[pos], mSplStart[pos + 1]);
                        if (mSplStart[pos + 1] < mSurfaceDecodedLen) {
                            mComposingStrDisplay += " ";
                        }
                    }
                    mActiveCmpsDisplayLen = mComposingStrDisplay.length();
                    if (mSurfaceDecodedLen < mSurface.length()) {
                        mComposingStrDisplay += mSurface
                                .substring(mSurfaceDecodedLen);
                    }
                }

                mFinishSelection = mSplStart.length == mFixedLen + 2;
            } catch (RemoteException e) {
                Log.w(TAG, "PinyinDecoderService died", e);
            } catch (Exception e) {
                mTotalChoicesNum = 0;
                mComposingStr = "";
            }
            // Prepare page 0.
            if (!mFinishSelection) {
                preparePage(0);
            }
        }

        /**
         * 选择预报候选词
         *
         * @param choiceId
         */
        private void choosePredictChoice(int choiceId) {
            if (ImeState.STATE_PREDICT != mImeState || choiceId < 0
                    || choiceId >= mTotalChoicesNum) {
                return;
            }

            String tmp = mCandidatesList.get(choiceId);

            resetCandidates();

            mCandidatesList.add(tmp);
            mTotalChoicesNum = 1;

            mSurface.replace(0, mSurface.length(), "");
            mCursorPos = 0;
            mFullSent = tmp;
            mFixedLen = tmp.length();
            mComposingStr = mFullSent;
            mActiveCmpsLen = mFixedLen;

            mFinishSelection = true;
        }

        /**
         * 获得指定的候选词
         *
         * @param candId
         * @return
         */
        String getCandidate(int candId) {
            // Only loaded items can be gotten, so we use mCandidatesList.size()
            // instead mTotalChoiceNum.
            if (candId < 0 || candId > mCandidatesList.size()) {
                return null;
            }
            return mCandidatesList.get(candId);
        }

        /**
         * 从缓存中获取一页的候选词，然后放进mCandidatesList中。三种不同的获取方式：1、mIPinyinDecoderService.
         * imGetChoiceList（）；2、mIPinyinDecoderService.imGetPredictList；3、从mAppCompletions[]取。
         */
        private void getCandiagtesForCache() {
            int fetchStart = mCandidatesList.size();
            int fetchSize = mTotalChoicesNum - fetchStart;
            if (fetchSize > MAX_PAGE_SIZE_DISPLAY) {
                fetchSize = MAX_PAGE_SIZE_DISPLAY;
            }
            try {
                List<String> newList = null;
                if (ImeState.STATE_INPUT == mImeState ||
                        ImeState.STATE_IDLE == mImeState ||
                        ImeState.STATE_COMPOSING == mImeState) {
                    newList = mIPinyinDecoderService.imGetChoiceList(
                            fetchStart, fetchSize, mFixedLen);
                } else if (ImeState.STATE_PREDICT == mImeState) {
                    newList = mIPinyinDecoderService.imGetPredictList(
                            fetchStart, fetchSize);
                } else if (ImeState.STATE_APP_COMPLETION == mImeState) {
                    newList = new ArrayList<String>();
                    if (null != mAppCompletions) {
                        for (int pos = fetchStart; pos < fetchSize; pos++) {
                            CompletionInfo ci = mAppCompletions[pos];
                            if (null != ci) {
                                CharSequence s = ci.getText();
                                if (null != s) {
                                    newList.add(s.toString());
                                }
                            }
                        }
                    }
                }
                mCandidatesList.addAll(newList);
            } catch (RemoteException e) {
                Log.w(TAG, "PinyinDecoderService died", e);
            }
        }

        /**
         * 判断指定页是否准备好了？
         *
         * @param pageNo
         * @return
         */
        public boolean pageReady(int pageNo) {
            // If the page number is less than 0, return false
            if (pageNo < 0) {
                return false;
            }

            // Page pageNo's ending information is not ready.
            return mPageStart.size() > pageNo + 1;
        }

        /**
         * 准备指定页，从缓存中取出指定页的候选词。
         *
         * @param pageNo
         * @return
         */
        public boolean preparePage(int pageNo) {
            // If the page number is less than 0, return false
            if (pageNo < 0) {
                return false;
            }

            // Make sure the starting information for page pageNo is ready.
            if (mPageStart.size() <= pageNo) {
                return false;
            }

            // Page pageNo's ending information is also ready.
            if (mPageStart.size() > pageNo + 1) {
                return true;
            }

            // If cached items is enough for page pageNo.
            if (mCandidatesList.size() - mPageStart.elementAt(pageNo) >= MAX_PAGE_SIZE_DISPLAY) {
                return true;
            }

            // Try to get more items from engine
            getCandiagtesForCache();

            // Try to find if there are available new items to display.
            // If no new item, return false;
            if (mPageStart.elementAt(pageNo) >= mCandidatesList.size()) {
                return false;
            }

            // If there are new items, return true;
            return true;
        }

        /**
         * 准备预报候选词
         *
         * @param history
         */
        void preparePredicts(String history) {
            if (null == history) {
                return;
            }
            resetCandidates();
            try {
                mTotalChoicesNum = mIPinyinDecoderService.imGetPredictsNum(history);
            } catch (RemoteException e) {
                return;
            }

            preparePage(0);
            mFinishSelection = false;
        }

        /**
         * 准备从app获取候选词
         *
         * @param completions
         */
        private void prepareAppCompletions(CompletionInfo completions[]) {
            resetCandidates();
            mAppCompletions = completions;
            mTotalChoicesNum = completions.length;
            preparePage(0);
            mFinishSelection = false;
        }

        /**
         * 获取当前页的长度
         *
         * @param currentPage
         * @return
         */
        public int getCurrentPageSize(int currentPage) {
            if (mPageStart.size() <= currentPage + 1) {
                return 0;
            }
            return mPageStart.elementAt(currentPage + 1)
                    - mPageStart.elementAt(currentPage);
        }

        /**
         * 获取当前页的开始位置
         *
         * @param currentPage
         * @return
         */
        public int getCurrentPageStart(int currentPage) {
            if (mPageStart.size() < currentPage + 1) {
                return mTotalChoicesNum;
            }
            return mPageStart.elementAt(currentPage);
        }

        /**
         * 是否还有下一页？
         *
         * @param currentPage
         * @return
         */
        public boolean pageForwardable(int currentPage) {
            if (mPageStart.size() <= currentPage + 1) {
                return false;
            }
            if (mPageStart.elementAt(currentPage + 1) >= mTotalChoicesNum) {
                return false;
            }
            return true;
        }

        /**
         * 是否有上一页
         *
         * @param currentPage
         * @return
         */
        public boolean pageBackwardable(int currentPage) {
            return currentPage > 0;
        }

        /**
         * 光标前面的字符是否是分隔符“'”
         *
         * @return
         */
        boolean charBeforeCursorIsSeparator() {
            int len = mSurface.length();
            if (mCursorPos > len) {
                return false;
            }
            if (mCursorPos > 0 && mSurface.charAt(mCursorPos - 1) == '\'') {
                return true;
            }
            return false;
        }

        /**
         * 获取光标位置
         *
         * @return
         */
        public int getCursorPos() {
            return mCursorPos;
        }

        /**
         * 获取光标在拼音字符串中的位置
         *
         * @return
         */
        int getCursorPosInCmps() {
            int cursorPos = mCursorPos;
            int fixedLen = 0;

            for (int hzPos = 0; hzPos < mFixedLen; hzPos++) {
                if (mCursorPos >= mSplStart[hzPos + 2]) {
                    cursorPos -= mSplStart[hzPos + 2] - mSplStart[hzPos + 1];
                    cursorPos += 1;
                }
            }
            return cursorPos;
        }

        /**
         * 获取光标在显示的拼音字符串中的位置
         *
         * @return
         */
        public int getCursorPosInCmpsDisplay() {
            int cursorPos = getCursorPosInCmps();
            // +2 is because: one for mSplStart[0], which is used for other
            // purpose(The length of the segmentation string), and another
            // for the first spelling which does not need a space before it.
            for (int pos = mFixedLen + 2; pos < mSplStart.length - 1; pos++) {
                if (mCursorPos <= mSplStart[pos]) {
                    break;
                } else {
                    cursorPos++;
                }
            }
            return cursorPos;
        }

        /**
         * 移动光标到末尾
         *
         * @param left
         */
        public void moveCursorToEdge(boolean left) {
            if (left) {
                mCursorPos = 0;
            } else {
                mCursorPos = mSurface.length();
            }
        }

        // Move cursor. If offset is 0, this function can be used to adjust
        // the cursor into the bounds of the string.
        // 移动光标
        public void moveCursor(int offset) {
            if (offset > 1 || offset < -1) {
                return;
            }

            if (offset != 0) {
                int hzPos = 0;
                for (hzPos = 0; hzPos <= mFixedLen; hzPos++) {
                    if (mCursorPos == mSplStart[hzPos + 1]) {
                        if (offset < 0) {
                            if (hzPos > 0) {
                                offset = mSplStart[hzPos]
                                        - mSplStart[hzPos + 1];
                            }
                        } else {
                            if (hzPos < mFixedLen) {
                                offset = mSplStart[hzPos + 2]
                                        - mSplStart[hzPos + 1];
                            }
                        }
                        break;
                    }
                }
            }
            mCursorPos += offset;
            if (mCursorPos < 0) {
                mCursorPos = 0;
            } else if (mCursorPos > mSurface.length()) {
                mCursorPos = mSurface.length();
            }
        }

        /**
         * 获取拼写字符串的数量
         *
         * @return
         */
        public int getSplNum() {
            return mSplStart[0];
        }

        /**
         * 获取固定的字符的数量
         *
         * @return
         */
        public int getFixedLen() {
            return mFixedLen;
        }
    }
}
