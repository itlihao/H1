package com.hospital.s1m.lib_base.entity;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


/**
 * @author android
 * @date 2018/3/7.
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
   private int left;
   private int right;
   private int top;
   private int bottom;
    public LinearItemDecoration(int top, int bottom, int left, int right) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = left;
        outRect.right = right;
        outRect.bottom = bottom;
        outRect.top = top;

    }


}