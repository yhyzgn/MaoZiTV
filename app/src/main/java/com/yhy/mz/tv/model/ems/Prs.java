package com.yhy.mz.tv.model.ems;

/**
 * 解析平台枚举
 * <p>
 * Created on 2023-01-29 21:41
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Prs {
    PAN_GU("盘古", "https://www.pangujiexi.cc/jiexi.php?url="),
    OK("OK解析", "https://okjx.cc/?url="),
    CK("CK解析", "https://www.ckplayer.vip/jiexi/?url="),
    IM_1907("IM1907", "https://im1907.top/?jx="),
    JSON_PLAYER("JsonPlayer", "https://jx.jsonplayer.com/player/?url="),
    ;

    private final String name;
    private final String url;

    Prs(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
