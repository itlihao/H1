package com.hospital.s1m.lib_base.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by Lihao on 2019-1-11.
 * Email heaolihao@163.com
 */
public class MyLinearLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled = true;

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyLinearLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyLinearLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}

