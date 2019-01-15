package com.zhiyihealth.registration.lib_base.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiyihealth.registration.lib_base.R;


/**
 * created by carbs.wang 2016/05/17
 * @author Lihao
 */
public class MDDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = MDDialog.class.getSimpleName();
    private LinearLayout llContent;

    //datas
    private Wrapper wrapper;

    private void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    private MDDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mddialog);
        hideBottomUIMenu();
        initWindow();
        initView();
    }

    private void initWindow() {

        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = (int) (wrapper.widthRatio * getScreenWidth(getContext()));
        if (wrapper.widthMaxDp != 0 && lp.width > dp2px(getContext(), wrapper.widthMaxDp)) {
            lp.width = dp2px(getContext(), wrapper.widthMaxDp);
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        win.setAttributes(lp);

        this.setCanceledOnTouchOutside(false);
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    private void initView() {
        RelativeLayout llContainer = this.findViewById(R.id.ll_container);
        RelativeLayout rlTitle = this.findViewById(R.id.rl_title);
        ImageView ivTitle = this.findViewById(R.id.iv_title);
        TextView tvTitle = this.findViewById(R.id.tv_title);
        TextView tvMessage = this.findViewById(R.id.tv_message);
        llContent = this.findViewById(R.id.ll_content);
        LinearLayout rlButtons = this.findViewById(R.id.rl_buttons);
        Button btnYes = this.findViewById(R.id.btn_yes);
        Button btnNo = this.findViewById(R.id.btn_no);
        AVLoadingIndicatorView avi = this.findViewById(R.id.av_avi);
        if (wrapper == null) {
            wrapper = new Wrapper();
        }
        if (!wrapper.showTitle) {
            rlTitle.setVisibility(View.GONE);
        }
        if (!wrapper.showMessage) {
            tvMessage.setVisibility(View.GONE);
        }
        if (!wrapper.showButtons) {
            rlButtons.setVisibility(View.GONE);
        }
        if (!wrapper.showNegativeButton) {
            btnNo.setVisibility(View.GONE);
        }
        if (!wrapper.showPositiveButton) {
            btnYes.setVisibility(View.GONE);
            /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnNo.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);*/
        }

        if (wrapper.icon != null) {
            ivTitle.setImageDrawable(wrapper.icon);
            ivTitle.setVisibility(View.VISIBLE);
            tvTitle.setPadding(0, 0, 0, 0);
        }

        if (wrapper.showAvi) {
            avi.show();
        } else {
            avi.hide();
        }

        tvTitle.setText(wrapper.title);
        tvTitle.setTextColor(wrapper.primaryTextColor);
        tvMessage.setText(wrapper.message);
        btnYes.setTag(BUTTON_POSITIVE_INDEX);
        btnYes.setOnClickListener(this);
//        btn_yes.setTextColor(wrapper.primaryTextColor);
        if (!TextUtils.isEmpty(wrapper.btTextYes)) {
            btnYes.setText(wrapper.btTextYes);
        }
        btnNo.setTag(BUTTON_NEGATIVE_INDEX);
        btnNo.setOnClickListener(this);
        if (!TextUtils.isEmpty(wrapper.btTextNo)) {
            btnNo.setText(wrapper.btTextNo);
        }
        llContainer.setBackgroundDrawable(getRoundRectShapeDrawable(getContext(), wrapper.cornerRadiusDp, Color.WHITE));
        /*btnYes.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));
        btnNo.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));*/

        analyseContent();
    }

    private static final int BUTTON_POSITIVE_INDEX = -1;
    private static final int BUTTON_NEGATIVE_INDEX = -2;

    private void analyseContent() {
        if (wrapper.messages == null || wrapper.messages.length == 0) {
            if (wrapper.contentView != null) {
                llContent.addView(wrapper.contentView);
            } else if (wrapper.contentViewLayoutResId != 0) {
                LayoutInflater.from(getContext()).inflate(wrapper.contentViewLayoutResId, llContent);
            }
            if (wrapper.contentViewOperator != null && llContent.getChildCount() != 0) {
                wrapper.contentViewOperator.operate(llContent.getChildAt(0));
            }
            return;
        }

        int itemPadding = dp2px(getContext(), wrapper.contentPaddingDp);
        int itemHeight = dp2px(getContext(), wrapper.contentItemHeightDp);
        for (int i = 0; i < wrapper.messages.length; i++) {
            TextView tv = new TextView(getContext());
            tv.setText(wrapper.messages[i]);
            tv.setTextSize(wrapper.contentTextSizeDp);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setOnClickListener(this);
            tv.setClickable(wrapper.contentViewClickable);
            tv.setTextColor(wrapper.contentTextColor);
            tv.setTag(i);
            tv.setMinHeight(itemHeight);
            if (wrapper.messages.length == 1) {
                if (wrapper.showTitle && wrapper.showButtons) {
                    tv.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));
                } else if (wrapper.showTitle && !wrapper.showButtons) {
                    tv.setBackgroundDrawable(getStateListDrawableForBottomItem(getContext(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                } else if (!wrapper.showTitle && wrapper.showButtons) {
                    tv.setBackgroundDrawable(getStateListDrawableForTopItem(getContext(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                } else {
                    tv.setBackgroundDrawable(getStateListDrawable(getContext(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                }
            } else {
                if (i == 0) {
                    if (wrapper.showTitle) {
                        tv.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));
                    } else {
                        tv.setBackgroundDrawable(getStateListDrawableForTopItem(getContext(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                    }
                } else if (i == wrapper.messages.length - 1) {
                    if (wrapper.showButtons) {
                        tv.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));
                    } else {
                        tv.setBackgroundDrawable(getStateListDrawableForBottomItem(getContext(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                    }
                } else {
                    tv.setBackgroundDrawable(getStateListDrawable(getContext(), 0, 0xdddddddd, 0x00000000));
                }
            }
            tv.setPadding(itemPadding, 0, itemPadding, 0);
            llContent.addView(tv);
            if (i != wrapper.messages.length - 1) {
                View divider = new View(getContext());
                divider.setBackgroundColor(wrapper.contentDividerColor);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerParams.setMargins(dp2px(getContext(), wrapper.dividerMarginHorizontalDp), 0,
                        dp2px(getContext(), wrapper.dividerMarginHorizontalDp), 0);
                divider.setLayoutParams(dividerParams);
                llContent.addView(divider);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (o != null && o instanceof Integer) {
            if (((Integer) o) == BUTTON_POSITIVE_INDEX) {
                if (wrapper.btListenerYes != null) {
                    wrapper.btListenerYes.onClick(v);
                }
                if (wrapper.btMultiListenerYes != null && (wrapper.contentView != null || wrapper.contentViewLayoutResId != 0)) {
                    if (llContent.getChildCount() > 0) {
                        wrapper.btMultiListenerYes.onClick(v, llContent.getChildAt(0));
                    }
                }
                if (wrapper.canClickDismisPosiviveBUtton) {
                    dismiss();
                }
            } else if (((Integer) o) == BUTTON_NEGATIVE_INDEX) {
                if (wrapper.btListenerNo != null) {
                    wrapper.btListenerNo.onClick(v);
                }
                if (wrapper.btMultiListenerNo != null && (wrapper.contentView != null || wrapper.contentViewLayoutResId != 0)) {
                    if (llContent.getChildCount() > 0) {
                        wrapper.btMultiListenerNo.onClick(v, llContent.getChildAt(0));
                    }
                }
            } else if (((Integer) o) >= 0) {
                if (wrapper.onItemClickListener != null) {
                    wrapper.onItemClickListener.onItemClicked((Integer) o);
                }
            }
        }
//        dismiss();
    }

    interface OnItemClickListener {
        void onItemClicked(int index);
    }

    public interface ContentViewOperator {
        void operate(View contentView);
    }

    interface OnMultiClickListener {
        void onClick(View clickedView, View contentView);
    }

    private static class Wrapper {
        public CharSequence title;
        public Context context;
        private CharSequence[] messages;
        private CharSequence message;
        public Drawable icon;
        private CharSequence btTextYes;
        private CharSequence btTextNo;
        private View.OnClickListener btListenerYes;
        private View.OnClickListener btListenerNo;
        private OnMultiClickListener btMultiListenerYes;
        private OnMultiClickListener btMultiListenerNo;
        private OnCancelListener btListenerCancel;
        private OnDismissListener btListenerDismiss;
        private OnItemClickListener onItemClickListener;
        private View contentView;//content view
        private ContentViewOperator contentViewOperator;
        private int contentViewLayoutResId;
        private boolean cancelable = false;
        private boolean showTitle = true;
        private boolean showMessage = true;
        private boolean showButtons = true;
        private boolean showNegativeButton = true;
        private boolean showPositiveButton = true;
        private boolean showAvi = false;
        private boolean contentViewClickable = true;
        private boolean canClickDismisPosiviveBUtton = false;
        private float widthRatio = 0.9f;
        private int widthMaxDp = 0;
        private int cornerRadiusDp = 24;
        private int dividerMarginHorizontalDp = 12;
        private int primaryTextColor = 0xff666666;
        private int contentTextColor = 0xaa111111;
        private int contentDividerColor = 0x44999999;
        private int contentPaddingDp = 16;
        private int contentItemHeightDp = 56;
        private int contentTextSizeDp = 18;
    }

    public static class Builder {

        private final Wrapper wrapper;

        public Builder(Context context) {
            wrapper = new Wrapper();
            wrapper.context = context;
        }

        public Context getContext() {
            return wrapper.context;
        }

        public Builder setTitle(int titleId) {
            wrapper.title = wrapper.context.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            wrapper.title = title.toString();
            return this;
        }

        public Builder setMessages(String message) {
            wrapper.message = message;
            return this;
        }

        public Builder setMessages(int messagesId) {
            wrapper.message = wrapper.context.getResources().getTextArray(messagesId)[0];
            return this;
        }

        public Builder setMessages(CharSequence[] messages) {
            wrapper.messages = messages;
            return this;
        }

        public Builder setIcon(int iconId) {
            wrapper.icon = wrapper.context.getResources().getDrawable(iconId);
            return this;
        }

        public Builder setIcon(Drawable icon) {
            wrapper.icon = icon;
            return this;
        }

        public Builder setPositiveButton(final View.OnClickListener listener) {
            wrapper.btListenerYes = listener;
            return this;
        }

        public Builder setPositiveButtonClickDismis(boolean canDis) {
            wrapper.canClickDismisPosiviveBUtton = canDis;
            return this;
        }

        public Builder setPositiveButton(int textId, final View.OnClickListener listener) {
            wrapper.btTextYes = wrapper.context.getText(textId);
            wrapper.btListenerYes = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
            wrapper.btTextYes = text;
            wrapper.btListenerYes = listener;
            return this;
        }

        public Builder setPositiveButtonMultiListener(final OnMultiClickListener btMultiListenerYes) {
            wrapper.btMultiListenerYes = btMultiListenerYes;
            return this;
        }

        public Builder setNegativeButton(final View.OnClickListener listener) {
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final View.OnClickListener listener) {
            wrapper.btTextNo = wrapper.context.getText(textId);
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
            wrapper.btTextNo = text;
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButtonMultiListener(final OnMultiClickListener btMultiListenerNo) {
            wrapper.btMultiListenerNo = btMultiListenerNo;
            return this;
        }

        public Builder setShowNegativeButton(boolean showNegativeButton) {
            wrapper.showNegativeButton = showNegativeButton;
            return this;
        }

        public Builder setShowPositiveButton(boolean showPositiveButton) {
            wrapper.showPositiveButton = showPositiveButton;
            return this;
        }

        public Builder setShowAvi(boolean showAvi) {
            wrapper.showAvi = showAvi;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            wrapper.cancelable = cancelable;
            return this;
        }

        public Builder setShowTitle(boolean showTitle) {
            wrapper.showTitle = showTitle;
            return this;
        }

        public Builder setShowMessage(boolean showMessage) {
            wrapper.showMessage = showMessage;
            return this;
        }

        public Builder setShowButtons(boolean showButtons) {
            wrapper.showButtons = showButtons;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            wrapper.btListenerCancel = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            wrapper.btListenerDismiss = onDismissListener;
            return this;
        }

        public Builder setOnItemClickListener(final OnItemClickListener listener) {
            wrapper.onItemClickListener = listener;
            return this;
        }

        public Builder setContentView(int layoutResId) {
            wrapper.contentView = null;
            wrapper.contentViewLayoutResId = layoutResId;
            return this;
        }

        public Builder setContentViewClickable(boolean clickable) {
            wrapper.contentViewClickable = clickable;
            return this;
        }

        public Builder setContentView(View contentView) {
            wrapper.contentView = contentView;
            wrapper.contentViewLayoutResId = 0;
            return this;
        }

        public Builder setWidthRatio(float widthRatio) {
            wrapper.widthRatio = widthRatio;
            return this;
        }

        public Builder setWidthMaxDp(int widthMaxDp) {
            wrapper.widthMaxDp = widthMaxDp;
            return this;
        }

        public Builder setBackgroundCornerRadius(int cornerRadiusDp) {
            wrapper.cornerRadiusDp = cornerRadiusDp;
            return this;
        }

        public Builder setDividerMarginHorizontalDp(int marginHoriDp) {
            wrapper.dividerMarginHorizontalDp = marginHoriDp;
            return this;
        }

        public Builder setPrimaryTextColor(int primaryTextColor) {
            wrapper.primaryTextColor = primaryTextColor;
            return this;
        }

        public Builder setContentTextColor(int contentTextColor) {
            wrapper.contentTextColor = contentTextColor;
            return this;
        }

        public Builder setContentDividerColor(int contentDividerColor) {
            wrapper.contentDividerColor = contentDividerColor;
            return this;
        }

        public Builder setContentPaddingDp(int contentPaddingDp) {
            wrapper.contentPaddingDp = contentPaddingDp;
            return this;
        }

        public Builder setContentItemHeightDp(int contentItemHeightDp) {
            wrapper.contentItemHeightDp = contentItemHeightDp;
            return this;
        }

        public Builder setContentTextSizeDp(int contentTextSizeDp) {
            wrapper.contentTextSizeDp = contentTextSizeDp;
            return this;
        }

        public Builder setContentViewOperator(ContentViewOperator operator) {
            wrapper.contentViewOperator = operator;
            return this;
        }

        public MDDialog create() {
            final MDDialog dialog = new MDDialog(wrapper.context);
            dialog.setCancelable(wrapper.cancelable);
            dialog.setOnCancelListener(wrapper.btListenerCancel);
            dialog.setOnDismissListener(wrapper.btListenerDismiss);
            dialog.setWrapper(wrapper);
            return dialog;
        }

        public MDDialog show() {
            MDDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    private Drawable getRoundRectShapeDrawable(float[] cornerRadiusPx, int color) {
        RoundRectShape rr = new RoundRectShape(cornerRadiusPx, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    private Drawable getRoundRectShapeDrawable(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx,
                cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private Drawable getRoundRectShapeDrawableForTopItem(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, 0, 0, 0, 0};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private Drawable getRoundRectShapeDrawableForBottomItem(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{0, 0, 0, 0, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    private Drawable getStateListDrawable(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawable(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawable(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }

    private Drawable getStateListDrawableForTopItem(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawableForTopItem(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawableForTopItem(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }

    private Drawable getStateListDrawableForBottomItem(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawableForBottomItem(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawableForBottomItem(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    @SuppressLint("ObsoleteSdkInt")
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
