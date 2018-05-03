package com.tomato.hystrix.demo.config;

public enum ErrorType {

    BADREQUEST("bad"),

    TIMEOUT("timeout"),

    RUNTIME_EXCEPTION("runtime"),

    NETWORK_EXCEPTION("network"),

    SUCCESS("success");

    private final String type;

    private ErrorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
