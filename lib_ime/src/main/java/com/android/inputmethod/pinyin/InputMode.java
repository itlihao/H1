package com.android.inputmethod.pinyin;

/**
 *
 * @author Lihao
 * @date 2019-1-2
 * Email heaolihao@163.com
 */
public class InputMode {
    /**
     * Bits used to indicate soft keyboard layout. If none bit is set, the
     * current input mode does not require a soft keyboard.
     **/
    private static final int MASK_SKB_LAYOUT = 0xf0000000;
    /**
     * A kind of soft keyboard layout. An input mode should be anded with
     */
    private static final int MASK_SKB_LAYOUT_QWERTY = 0x10000000;

    private static final int MASK_LANGUAGE = 0x0f000000;

    /**
     * Used to indicate the current language. An input mode should be anded with
     */
    private static final int MASK_LANGUAGE_CN = 0x01000000;
    /**
     * Used to indicate the current language. An input mode should be anded with
     */
    private static final int MASK_LANGUAGE_EN = 0x02000000;
    /**
     * Mode for inputing English with a hardware keyboard
     */
    private static final int MODE_HKB_ENGLISH = (MASK_LANGUAGE_EN);
    /**
     * Unset mode.
     */
    private static final int MODE_UNSET = 0;

    /**
     * The input mode for the current edit box.
     */
    private int mInputMode = MODE_SKB_CHINESE;

    private int mPreviousInputMode = MODE_SKB_CHINESE;

    /**
     * Mode for inputing Chinese with soft keyboard.
     */
    public static final int MODE_SKB_CHINESE = (MASK_SKB_LAYOUT_QWERTY | MASK_LANGUAGE_CN);

    private static final int MASK_CASE_LOWER = 0x00100000;

    private static final int MASK_CASE_UPPER = 0x00200000;

    /**
     * Mode for inputing English lower characters with soft keyboard.
     */
    public static final int MODE_SKB_ENGLISH_LOWER = (MASK_SKB_LAYOUT_QWERTY
            | MASK_LANGUAGE_EN | MASK_CASE_LOWER);

    /**
     * Mode for inputing English upper characters with soft keyboard.
     */
    public static final int MODE_SKB_ENGLISH_UPPER = (MASK_SKB_LAYOUT_QWERTY
            | MASK_LANGUAGE_EN | MASK_CASE_UPPER);

    /**
     * Mode for inputing Chinese with a hardware keyboard.
     */
    public static final int MODE_HKB_CHINESE = (MASK_LANGUAGE_CN);

    /**
     * Used to remember recent mode to input language.
     */
    private int mRecentLauageInputMode = MODE_SKB_CHINESE;


    public boolean isEnglishWithHkb() {
        return MODE_HKB_ENGLISH == mInputMode;
    }

    public boolean isEnglishWithSkb() {
        return MODE_SKB_ENGLISH_LOWER == mInputMode || MODE_SKB_ENGLISH_UPPER == mInputMode;
    }

    public boolean isEnglishUpperCaseWithSkb() {
        return MODE_SKB_ENGLISH_UPPER == mInputMode;
    }

    public boolean isChineseText() {
        int skbLayout = (mInputMode & MASK_SKB_LAYOUT);
        if (MASK_SKB_LAYOUT_QWERTY == skbLayout || 0 == skbLayout) {
            int language = (mInputMode & MASK_LANGUAGE);
            return MASK_LANGUAGE_CN == language;
        }
        return false;
    }

    public void switchChinese() {
        saveInputMode(MODE_SKB_CHINESE);
    }

    public void switchEnglish() {
        saveInputMode(MODE_SKB_ENGLISH_LOWER);
    }

    public void switchShift(boolean isUpper) {
        if (isUpper) {
            saveInputMode(MODE_SKB_ENGLISH_UPPER);
        } else {
            saveInputMode(MODE_SKB_ENGLISH_LOWER);
        }
    }

    private void saveInputMode(int newInputMode) {
        mPreviousInputMode = mInputMode;
        mInputMode = newInputMode;

        int skbLayout = (mInputMode & MASK_SKB_LAYOUT);
        if (MASK_SKB_LAYOUT_QWERTY == skbLayout || 0 == skbLayout) {
            mRecentLauageInputMode = mInputMode;
        }

        /*if (!Environment.getInstance().hasHardKeyboard()) {
            mInputIcon = 0;
        }*/
    }
}
