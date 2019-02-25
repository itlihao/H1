package com.hospital.s1m.lib_base.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class EmnuLeft implements MultiItemEntity {


    /**
     * 普通条纹
     */
    public static final int ITEM_TITLE = 0;
    /**
     * 快速挂号
     */
    public static  final int ITEM_IMAGE = 1;

    public EmnuLeft(String key, String name) {
        this.key = key;
        this.name = name;
        this.itemType = EmnuLeft.ITEM_TITLE;
    }

    public EmnuLeft(String key, String name, int itemType) {
        this.key = key;
        this.name = name;
        this.itemType = itemType;
    }

    private String key;
    private String name;
    private int itemType;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
