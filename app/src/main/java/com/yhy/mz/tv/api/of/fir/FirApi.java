package com.yhy.mz.tv.api.of.fir;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.yhy.mz.tv.api.callback.JsonCallback;
import com.yhy.mz.tv.api.model.FirVersionInfo;

import java.util.function.Consumer;

/**
 * Created on 2023-01-26 01:24
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class FirApi {
    private static final String FIR_TOKEN = "2488aa0abca0aaf89c88a999d79697b4";

    public static final FirApi instance = new FirApi();

    private FirApi() {
    }

    public void versionQuery(Consumer<FirVersionInfo> consumer) {
        OkGo.<FirVersionInfo>get("http://api.bq04.com/apps/latest/" + "com.yhy.mz.tv")
                .params("api_token", FIR_TOKEN)
                .params("type", "android")
                .execute(new JsonCallback<FirVersionInfo>() {
                    @Override
                    public void onSuccess(Response<FirVersionInfo> response) {
                        consumer.accept(response.body());
                    }
                });
    }
}
