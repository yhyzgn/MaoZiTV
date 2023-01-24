package com.yhy.mz.tv.model.ems;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created on 2023-01-23 23:24
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Chan {
    RECOMMEND(100, "推荐"),
    YI_FILM(200, "艺影"),
    YI_EPISODE(300, "艺剧"),
    XUN_FILM(400, "鹅影"),
    XUN_EPISODE(500, "鹅剧"),
    KU_FILM(600, "酷影"),
    KU_EPISODE(700, "酷剧"),
    ;

    private final Integer code;
    private final String name;

    Chan(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Chan parse(int code) {
        return Arrays.stream(Chan.values()).filter(it -> Objects.equals(it.code, code)).findFirst().orElse(RECOMMEND);
    }
}
