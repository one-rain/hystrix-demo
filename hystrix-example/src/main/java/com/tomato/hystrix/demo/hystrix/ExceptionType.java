package com.tomato.hystrix.demo.hystrix;

public enum ExceptionType {

    BADREQUEST("bad"),

    TIMEOUT("timeout"),

    RUNTIME_EXCEPTION("runtime"),

    NETWORK_EXCEPTION("network"),

    SUCCESS("success");

    private final String type;

    private ExceptionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
