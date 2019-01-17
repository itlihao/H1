package com.hospital.s1m.lib_print.bean;

/**
 * Created by Administrator on 2017/5/24.
 */

public class TableItem {
    private String[] text;
    private int[] width;
    private int[] align;

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public int[] getWidth() {
        return width;
    }

    public void setWidth(int[] width) {
        this.width = width;
    }

    public int[] getAlign() {
        return align;
    }

    public void setAlign(int[] align) {
        this.align = align;
    }
}
