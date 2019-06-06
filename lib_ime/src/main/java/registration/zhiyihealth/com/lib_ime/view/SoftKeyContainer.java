package registration.zhiyihealth.com.lib_ime.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.inputmethod.pinyin.NewCandidatesContainer;
import com.android.inputmethod.pinyin.NewComposingView;
import com.android.inputmethod.pinyin.SoftKey;

import java.util.List;

import registration.zhiyihealth.com.lib_ime.R;
import registration.zhiyihealth.com.lib_ime.listener.CandidateViewListener;
import registration.zhiyihealth.com.lib_ime.listener.SoftKeyListener;
import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;

/**
 * @author Lihao
 * @date 2018-12-28
 * Email heaolihao@163.com
 */
public class SoftKeyContainer extends LinearLayout {
    private static final String TAG = SoftKeyContainer.class.getSimpleName();
    private SoftKeyListener skListener;

    /**
     * 浮动视图集装箱
     */
    private LinearLayout mFloatingContainer;

    /**
     * View to show the composing string. 组成字符串的View，用于显示输入的拼音。
     */
    private NewComposingView mComposingView;

    /**
     * Window to show the composing string.用于输入拼音字符串的窗口。
     */
    private PopupWindow mFloatingWindow;

    /**
     * Used to show the floating window.显示输入的拼音字符串PopupWindow 定时器
     */
    private PopupTimer mFloatingWindowTimer;

    /**
     * View to show candidates list. 候选词视图集装箱
     */
    private NewCandidatesContainer mCandidatesContainer;

    /**
     * Used to notify the input method when the user touch a candidate.
     * 当用户选择了候选词或者在候选词视图滑动了手势时的通知输入法。 实现了候选词视图的监听器CandidateViewListener。
     */
    private ChoiceNotifier mChoiceNotifier;
    /**
     * The on-screen movement gesture detector for candidates view.候选词的手势检测器
     */
    private GestureDetector mGestureDetectorCandidates;

    private Context mContext;

    private MyKeyboardView keyboardView;
    // 字母键盘
    private Keyboard k1;
    // 数字键盘
    private Keyboard k2;

    private FrameLayout frameLayout;

    private int keyboardType = 1;
    // 是否数据键盘
    public boolean isNum = false;
    //是否标点键盘
    public boolean ispun = false;
    // 是否大写
    public boolean isupper = false;
    // 是否中文
    public boolean isCN = true;

    public static final int KEYCODE_PUN = -7;
    public static final int KEYCODE_SWITCH = -8;
    public static final int KEYCODE_CLOSE = -9;

    public SoftKeyContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.softkey_container, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initialize(Context context, SoftKeyListener cListener) {
        skListener = cListener;
        mContext = context;

        mCandidatesContainer = findViewById(R.id.candidate_container);

        k1 = new Keyboard(mContext, R.xml.qwerty);
        k2 = new Keyboard(mContext, R.xml.symbols);
//        k3 = new Keyboard(ctx, R.xml.punctuate);
        keyboardView = findViewById(R.id.keyboard_view);
        frameLayout = findViewById(R.id.search_edit_frame);
        keyboardView.setDelDrawable(ContextCompat.getDrawable(mContext, R.drawable.delete));
        keyboardView.setLowDrawable(ContextCompat.getDrawable(mContext, R.drawable.keyboard_shift));
        keyboardView.setUpDrawable(ContextCompat.getDrawable(mContext, R.drawable.keyboard_caps));
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);

        keyboardView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);

        mChoiceNotifier = new ChoiceNotifier();
        /*
         * Used to notify gestures from candidates view. 候选词的手势监听器
         */OnGestureListener mGestureListenerCandidates = new OnGestureListener(true);
        mGestureDetectorCandidates = new GestureDetector(context, mGestureListenerCandidates);
        onCreateCandidatesView();
    }

    @SuppressLint("InflateParams")
    public void onCreateCandidatesView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        // Inflate the floating container view 设置显示输入拼音字符串View的集装箱
        mFloatingContainer = (LinearLayout) inflater.inflate(R.layout.new_floating_container, null);

        // The first child is the composing view.
        mComposingView = (NewComposingView) mFloatingContainer.getChildAt(0);

        mCandidatesContainer.initialize(mContext, mChoiceNotifier, mGestureDetectorCandidates);

        // The floating window
        if (null != mFloatingWindow && mFloatingWindow.isShowing()) {
            mFloatingWindowTimer.cancelShowing();
            mFloatingWindow.dismiss();
        }

        // 中文输入，显示在候选区左边的浮动窗口.
        mFloatingWindow = new PopupWindow(this);
        mFloatingWindow.setClippingEnabled(false);
        mFloatingWindow.setBackgroundDrawable(null);
        mFloatingWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mFloatingWindow.setContentView(mFloatingContainer);
        mFloatingWindowTimer = new PopupTimer();

//        setCandidatesViewShown(true);
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
            if (keyboardType == 2) {
                keyboardView.setPreviewEnabled(false);
            } else {
                keyboardView.setPreviewEnabled(true);
                if (primaryCode == Keyboard.KEYCODE_CANCEL || primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == Keyboard.KEYCODE_SHIFT
                        || primaryCode == Keyboard.KEYCODE_MODE_CHANGE || primaryCode == KEYCODE_PUN
                        || primaryCode == KEYCODE_SWITCH || primaryCode == KEYCODE_CLOSE) {
                    keyboardView.setPreviewEnabled(false);
                } else {
                    keyboardView.setPreviewEnabled(true);
                }
            }
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            SoftKey softKey = new SoftKey();
            // 大小写切换
            if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                changeKey();
                keyboardType = 1;
                switchKeyboard();
                softKey.setKeyAttribute(primaryCode, "", true, true);
                skListener.onKeyClick(softKey, 1, isupper);
            }
            // 回退
            else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                softKey.setKeyAttribute(67, "", true, true);
                skListener.onKeyClick(softKey, 1, false);
            } else if (primaryCode == KEYCODE_SWITCH) {
                if (keyboardType == 2) {
                    keyboardType = 1;
                    switchKeyboard();
                } else {
                    changeCN();
                    softKey.setKeyAttribute(-8, "", true, true);
                    skListener.onKeyClick(softKey, 1, isCN);
                }
            }
            // 数字键盘切换
            else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                // 数字与字母键盘互换
                if (keyboardType == 2) {
                    keyboardType = 1;
                } else {
                    keyboardType = 2;
                }
                switchKeyboard();
            } else if (primaryCode == KEYCODE_CLOSE) {
                hideKeyboard();
            } else {
                softKey.setKeyAttribute(primaryCode, "", true, true);
                skListener.onKeyClick(softKey, 1, false);
            }
        }
    };

    public void setKeyBoard(int keyType) {
        keyboardType = keyType;
        switchKeyboard();
    }

    private void switchKeyboard() {
        switch (keyboardType) {
            case 1:
                keyboardView.setKeyboard(k1);
                break;
            case 2:
                changeText();
                hiddenComposeView();
                hiddenCandiatesContainer();
                keyboardView.setKeyboard(k2);
                break;
            default:
                Log.e(TAG, "ERROR keyboard type");
                break;
        }
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = k1.getKeys();
        //大写切换小写
        if (isupper) {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                }
            }
        }
        //小写切换大写
        else {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                }

                if (isCN) {
                    if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                        key.label = "英/中";

                        hiddenComposeView();
                        hiddenCandiatesContainer();
                    }
                }
            }
            if (isCN) {
                isCN = false;
            }
        }
        isupper = !isupper;
        keyboardView.setCap(isupper);

//        changeEng();
    }

    private void changeLowerCase() {
        if (isCN) {
            return;
        }
        List<Keyboard.Key> keylist = k1.getKeys();
        //大写切换小写
        if (isupper) {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                }
            }
        }
        isupper = false;
        keyboardView.setCap(false);
        keyboardView.setKeyboard(k1);
    }

    private void changeEng() {
        List<Keyboard.Key> keylist = k1.getKeys();
        if (isupper) {
            if (isCN) {
                for (Keyboard.Key key : keylist) {
                    if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                        key.label = "英/中";
                    }
                }
            }
            isCN = false;
        }
    }

    /**
     * 中英文切换
     */
    private void changeCN() {
        List<Keyboard.Key> keylist = k1.getKeys();
        if (isCN) {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                    key.label = "英/中";
                }
            }
        } else {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                    key.label = "中/英";
                }
            }
        }

        changeLowerCase();
        isCN = !isCN;
        keyboardView.setCN(isCN);
    }

    private void changeText() {
        List<Keyboard.Key> keylist = k2.getKeys();
        if (isCN) {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                    key.label = "中/英";
                }
            }
        } else {
            for (Keyboard.Key key : keylist) {
                if (key.label != null && key.codes[0] == KEYCODE_SWITCH) {
                    key.label = "英/中";
                }
            }
        }
    }

    public void showKeyboard() {
        /*int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }*/
        int visible = frameLayout.getVisibility();
        if (visible == View.GONE || visible == View.INVISIBLE) {
            frameLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        /*int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }*/
        int visible = frameLayout.getVisibility();
        if (visible == View.VISIBLE) {
            frameLayout.setVisibility(View.GONE);
        }

        hiddenComposeView();
        hiddenCandiatesContainer();
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 显示输入的拼音字符串PopupWindow 定时器
     *
     * @author keanbin
     * @ClassName PopupTimer
     */
    @SuppressLint("HandlerLeak")
    private class PopupTimer extends Handler implements Runnable {
        private int mParentLocation[] = new int[2];

        void postShowFloatingWindow() {
            mFloatingContainer.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mFloatingWindow.setWidth(mFloatingContainer.getMeasuredWidth());
            mFloatingWindow.setHeight(mFloatingContainer.getMeasuredHeight());
            post(this);
        }

        void cancelShowing() {
            if (mFloatingWindow.isShowing()) {
                mFloatingWindow.dismiss();
            }
            removeCallbacks(this);
        }

        @Override
        public void run() {
            mCandidatesContainer.getLocationInWindow(mParentLocation);

            if (!mFloatingWindow.isShowing()) {
                // 显示候选词PopupWindow
                if (mCandidatesContainer.getWindowToken() != null) {
                    mFloatingWindow.showAtLocation(mCandidatesContainer,
                            Gravity.START | Gravity.TOP, mParentLocation[0],
                            mParentLocation[1] - mFloatingWindow.getHeight());
                }
            } else {
                // 更新候选词PopupWindow
                if (mCandidatesContainer.getWindowToken() != null) {
                    mFloatingWindow.update(mParentLocation[0],
                            mParentLocation[1] - mFloatingWindow.getHeight(),
                            mFloatingWindow.getWidth(),
                            mFloatingWindow.getHeight());
                }
            }
        }
    }

    /**
     * 设置是否显示输入拼音的view
     *
     * @param visible
     */
    private void updateComposingText(boolean visible, PinYinManager.DecodingInfo info, PinYinManager.ImeState mImeState) {
        if (!visible) {
            mComposingView.setVisibility(View.INVISIBLE);
        } else {
            mComposingView.setDecodingInfo(info, mImeState);
            mComposingView.setVisibility(View.VISIBLE);
        }
        mComposingView.invalidate();
    }

    /**
     * 手势监听器
     */
    public class OnGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        /**
         * When user presses and drags, the minimum x-distance to make a
         * response to the drag event.当用户拖拽的时候，x轴上最小的差值才可以产生拖拽事件。
         */
        private static final int MIN_X_FOR_DRAG = 60;

        /**
         * When user presses and drags, the minimum y-distance to make a
         * response to the drag event.当用户拖拽的时候，y轴上最小的差值才可以产生拖拽事件。
         */
        private static final int MIN_Y_FOR_DRAG = 40;

        /**
         * Velocity threshold for a screen-move gesture. If the minimum
         * x-velocity is less than it, no gesture.
         * gesture.x轴上的手势的最小速率阀值，小于这个阀值，就不是手势。只要在滑动的期间
         * * ，有任意一段的速率小于这个值，就判断这次的滑动不是手势mNotGesture = true，就算接下去滑动的速率变高也是没用。
         */
        static private final float VELOCITY_THRESHOLD_X1 = 0.3f;

        /**
         * Velocity threshold for a screen-move gesture. If the maximum
         * x-velocity is less than it, no gesture.
         * x轴上的手势的最大速率阀值，大于这个阀值，就一定是手势，mGestureRecognized = true。
         */
        static private final float VELOCITY_THRESHOLD_X2 = 0.7f;

        /**
         * Velocity threshold for a screen-move gesture. If the minimum
         * y-velocity is less than it, no gesture.
         * * gesture.y轴上的手势的最小速率阀值，小于这个阀值，就不是手势。只要在滑动的期间
         * * ，有任意一段的速率小于这个值，就判断这次的滑动不是手势mNotGesture =
         * * true，就算接下去滑动的速率变高也是没用，mGestureRecognized = true。
         */
        static private final float VELOCITY_THRESHOLD_Y1 = 0.2f;

        /**
         * Velocity threshold for a screen-move gesture. If the maximum
         * y-velocity is less than it, no gesture.y轴上的手势的最大速率阀值，大于这个阀值，就一定是手势。
         */
        static private final float VELOCITY_THRESHOLD_Y2 = 0.45f;

        /**
         * If it false, we will not response detected gestures. 是否响应检测到的手势
         */
        private boolean mReponseGestures;

        /**
         * The minimum X velocity observed in the gesture.  能检测到的x最小速率的手势
         */
        private float mMinVelocityX = Float.MAX_VALUE;

        /**
         * The minimum Y velocity observed in the gesture. 能检测到y最小速率的手势
         */
        private float mMinVelocityY = Float.MAX_VALUE;

        /**
         * The first down time for the series of touch events for an action. 第一次触摸事件的时间
         */
        private long mTimeDown;

        /**
         * The last time when onScroll() is called. 最后一次 onScroll（）被调用的时间
         */
        private long mTimeLastOnScroll;

        /**
         * This flag used to indicate that this gesture is not a gesture.
         * 是否不是一个手势？
         */
        private boolean mNotGesture;

        /**
         * This flag used to indicate that this gesture has been recognized. \
         * 是否是一个公认的手势？
         */

        private boolean mGestureRecognized;

        OnGestureListener(boolean reponseGestures) {
            mReponseGestures = reponseGestures;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mMinVelocityX = Integer.MAX_VALUE;
            mMinVelocityY = Integer.MAX_VALUE;
            mTimeDown = e.getEventTime();
            mTimeLastOnScroll = mTimeDown;
            mNotGesture = false;
            mGestureRecognized = false;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mNotGesture) {
                return false;
            }
            if (mGestureRecognized) {
                return true;
            }

            if (Math.abs(e1.getX() - e2.getX()) < MIN_X_FOR_DRAG && Math.abs(e1.getY() - e2.getY()) < MIN_Y_FOR_DRAG) {
                return false;
            }

            long timeNow = e2.getEventTime();
            long spanTotal = timeNow - mTimeDown;
            long spanThis = timeNow - mTimeLastOnScroll;
            if (0 == spanTotal) {
                spanTotal = 1;
            }
            if (0 == spanThis) {
                spanThis = 1;
            }

            // 计算总速率
            float vXTotal = (e2.getX() - e1.getX()) / spanTotal;
            float vYTotal = (e2.getY() - e1.getY()) / spanTotal;

            // The distances are from the current point to the previous one.
            //计算这次 onScroll 的速率
            float vXThis = -distanceX / spanThis;
            float vYThis = -distanceY / spanThis;

            float kX = vXTotal * vXThis;
            float kY = vYTotal * vYThis;
            float k1 = kX + kY;
            float k2 = Math.abs(kX) + Math.abs(kY);
            // TODO 这个是什么计算公式？
            if (k1 / k2 < 0.8) {
                mNotGesture = true;
                return false;
            }
            float absVXTotal = Math.abs(vXTotal);
            float absVYTotal = Math.abs(vYTotal);
            if (absVXTotal < mMinVelocityX) {
                mMinVelocityX = absVXTotal;
            }
            if (absVYTotal < mMinVelocityY) {
                mMinVelocityY = absVYTotal;
            }
            // 如果最小的速率比规定的小，那么就不是手势。
            if (mMinVelocityX < VELOCITY_THRESHOLD_X1 && mMinVelocityY < VELOCITY_THRESHOLD_Y1) {
                mNotGesture = true;
                return false;
            }
            // 判断是什么手势？并调用手势处理函数。
            if (vXTotal > VELOCITY_THRESHOLD_X2 && absVYTotal < VELOCITY_THRESHOLD_Y2) {
                if (mReponseGestures) {
                    onDirectionGesture(Gravity.END);
                }
                mGestureRecognized = true;
            } else if (vXTotal < -VELOCITY_THRESHOLD_X2 && absVYTotal < VELOCITY_THRESHOLD_Y2) {
                if (mReponseGestures) {
                    onDirectionGesture(Gravity.START);
                }
                mGestureRecognized = true;
            } else if (vYTotal > VELOCITY_THRESHOLD_Y2 && absVXTotal < VELOCITY_THRESHOLD_X2) {
                if (mReponseGestures) {
                    onDirectionGesture(Gravity.BOTTOM);
                }
                mGestureRecognized = true;
            } else if (vYTotal < -VELOCITY_THRESHOLD_Y2 && absVXTotal < VELOCITY_THRESHOLD_X2) {
                if (mReponseGestures) {
                    onDirectionGesture(Gravity.TOP);
                }
                mGestureRecognized = true;
            }

            mTimeLastOnScroll = timeNow;
            return mGestureRecognized;
        }

        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            return mGestureRecognized;
        }

        /**
         * 手势的处理函数
         *
         * @param gravity 手势的类别
         */
        void onDirectionGesture(int gravity) {
            if (Gravity.NO_GRAVITY == gravity) {
                return;
            }

            if (Gravity.START == gravity || Gravity.END == gravity) {
                if (mCandidatesContainer.isShown()) {
                    if (Gravity.START == gravity) {
                        mCandidatesContainer.pageForward(true, true);
                    } else {
                        mCandidatesContainer.pageBackward(true, true);
                    }
                }
            }
        }
    }

    /**
     * Used to notify IME that the user selects a candidate or performs an
     * gesture. 当用户选择了候选词或者在候选词视图滑动了手势时的通知输入法。实现了候选词视图的监听器CandidateViewListener，
     * 有选择候选词的处理函数、手势向右滑动的处理函数、手势向左滑动的处理函数 、手势向上滑动的处理函数、手势向下滑动的处理函数。
     */
    @SuppressLint("HandlerLeak")
    public class ChoiceNotifier extends Handler implements CandidateViewListener {
        ChoiceNotifier() {

        }

        @Override
        public void onClickChoice(int choiceId) {
            if (choiceId >= 0) {
                SoftKey softKey = new SoftKey();
                softKey.setChoiceId(choiceId);
                PinYinManager.getInstance().onChoiceTouched(choiceId);
            }
        }

        @Override
        public void onToLeftGesture() {
//            if (PinyinIME.ImeState.STATE_COMPOSING == mImeState) {
//                changeToStateInput(true);
//            }
            mCandidatesContainer.pageBackward(true, false);
        }

        @Override
        public void onToRightGesture() {
//            if (PinyinIME.ImeState.STATE_COMPOSING == mImeState) {
//                changeToStateInput(true);
//            }
            mCandidatesContainer.pageForward(true, false);
        }

        @Override
        public void onToTopGesture() {
        }

        @Override
        public void onToBottomGesture() {
        }
    }

    public void showCandidateWindow(boolean showComposingView, PinYinManager.DecodingInfo info, PinYinManager.ImeState mImeState) {
        updateComposingText(showComposingView, info, mImeState);
        mCandidatesContainer.setVisibility(View.VISIBLE);
        mCandidatesContainer.showCandidates(info, PinYinManager.ImeState.STATE_COMPOSING != mImeState);
        mFloatingWindowTimer.postShowFloatingWindow();
    }

    public void resetState(boolean showComposingView, PinYinManager.DecodingInfo info, PinYinManager.ImeState mImeState) {
        // 重置显示输入拼音字符串的 View
        if (null != mComposingView) {
//            mComposingView.reset();
            mComposingView.setVisibility(View.INVISIBLE);
            mComposingView.invalidate();
        }

        try {
            mFloatingWindowTimer.cancelShowing();
            mFloatingWindow.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "Fail to show the PopupWindow.");
        }

        if (null != mCandidatesContainer && mCandidatesContainer.isShown()) {
            if (info.mCandidatesList.size() > 0) {
                mCandidatesContainer.setVisibility(View.VISIBLE);
            } else {
                mCandidatesContainer.setVisibility(View.GONE);
            }
            mCandidatesContainer.showCandidates(info, PinYinManager.ImeState.STATE_COMPOSING != mImeState);
            mFloatingWindowTimer.postShowFloatingWindow();
        }
    }

    private void hiddenCandiatesContainer() {
        if (null != mCandidatesContainer && mCandidatesContainer.isShown()) {
            mCandidatesContainer.setVisibility(View.GONE);
        }
    }

    public void hiddenComposeView() {
        if (null != mComposingView) {
            mComposingView.setVisibility(View.INVISIBLE);
            mComposingView.invalidate();
        }
    }
}
