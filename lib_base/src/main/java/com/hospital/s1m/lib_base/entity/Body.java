package com.hospital.s1m.lib_base.entity;

/**
 * Created by android on 2017/8/8.
 */

public class Body<T> {
    private String synCode;
    private Object code;
    private T param;
    private String content;

    public String getSynCode() {
        return synCode;
    }

    public void setSynCode(String synCode) {
        this.synCode = synCode;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
