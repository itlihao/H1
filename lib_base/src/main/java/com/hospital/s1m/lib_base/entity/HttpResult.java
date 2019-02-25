package com.hospital.s1m.lib_base.entity;

/**
 * Created by liukun on 16/3/5.
 */
public class HttpResult<T> {
    private Header header;

    private Body<T> body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body<T> getBody() {
        return body;
    }

    public void setBody(Body<T> body) {
        this.body = body;
    }
}
