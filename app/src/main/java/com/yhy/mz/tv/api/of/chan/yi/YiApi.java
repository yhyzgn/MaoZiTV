package com.yhy.mz.tv.api.of.chan.yi;

import android.text.TextUtils;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yhy.mz.tv.api.model.YiVideo;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.VideoType;
import com.yhy.mz.tv.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2023-01-24 21:08
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class YiApi {
    public static final YiApi instance = new YiApi();

    private final Gson gson;

    private YiApi() {
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    public List<Video> page(int page, VideoType type, int mode) throws Exception {
        ListenableFuture<List<Video>> future = new AbstractFuture<List<Video>>() {
            {
                OkGo.<String>get("https://mesh.if.iqiyi.com/portal/videolib/pcw/data")
                        .params("ret_num", 30)
                        .params("page_id", Math.max(1, page))
                        .params("channel_id", type == VideoType.FILM ? 1 : 2)
                        .params("mode", mode)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String result = response.body();
                                try {
                                    JSONObject jo = new JSONObject(result);
                                    JSONArray ja = jo.getJSONArray("data");
                                    String json = ja.toString();

                                    List<YiVideo> list = gson.fromJson(json, new TypeToken<List<YiVideo>>() {
                                    });
                                    List<Video> res = list.stream().map(it -> {
                                        Video vd = new Video();
                                        vd.id = it.filmId + "";
                                        vd.title = it.title;
                                        vd.description = it.description;
                                        vd.score = it.snsScore;
                                        vd.imgCover = it.imageUrlNormal;
                                        vd.pageUrl = it.pageUrl;
                                        vd.channel = "爱奇艺";
                                        vd.type = type;
                                        vd.tags = TextUtils.isEmpty(it.tag) ? null : Arrays.stream(it.tag.split(",")).collect(Collectors.toList());
                                        vd.directors = it.creator.stream().map(dto -> dto.name).collect(Collectors.toList());
                                        vd.actors = it.contributor.stream().map(dto -> dto.name).collect(Collectors.toList());

                                        return vd;
                                    }).collect(Collectors.toList());

                                    set(res);
                                } catch (JSONException e) {
                                    LogUtils.e(e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onError(Response<String> response) {
                                LogUtils.e(response.getException().getMessage());
                                setException(response.getException());
                            }
                        });
            }
        };
        return future.get();
    }
}
