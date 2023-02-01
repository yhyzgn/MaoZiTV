package com.yhy.mz.tv.api.of.chan.xun;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yhy.mz.tv.api.model.XunVideo;
import com.yhy.mz.tv.internal.Maps;
import com.yhy.mz.tv.model.Video;
import com.yhy.mz.tv.model.ems.VideoType;
import com.yhy.mz.tv.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2023-01-24 21:08
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class XunApi {
    public static final XunApi instance = new XunApi();

    private final Gson gson;

    private XunApi() {
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

    public List<Video> page(int page, VideoType type, int mode) throws Exception {
        ListenableFuture<List<Video>> future = new AbstractFuture<List<Video>>() {
            {
                String pg = (Math.max(1, page) - 1) + "";
                Map<String, Object> body = Maps.of(
                        "page_bypass_params", Maps.of(
                                "abtest_bypass_id", "f5aa0d25e4aa0b62",
                                "params", Maps.of(
                                        "caller_id", "3000010",
                                        "channel_id", "100173",
                                        "data_mode", "default",
                                        "filter_params", "itype=-1&iarea=-1&characteristic=-1&year=-1&charge=-1&sort=75",
                                        "page", pg,
                                        "page_id", "channel_list_second_page",
                                        "page_type", "operation",
                                        "platform_id", "2",
                                        "user_mode", "default"
                                ),
                                "scene", "operation"
                        ),
                        "page_context", Maps.of("page_index", pg),
                        "page_params", Maps.of(
                                "page_id", "channel_list_second_page",
                                "page_type", "operation",
                                "channel_id", type == VideoType.FILM ? "100173" : type == VideoType.EPISODE ? "100113" : "100105",
                                "filter_params", "itype=-1&iarea=-1&characteristic=-1&year=-1&charge=-1&sort=75",
                                "page", pg
                        ));

                OkGo.<String>post("https://pbaccess.video.qq.com/trpc.vector_layout.page_view.PageService/getPage?video_appid=3000010")
                        .headers("cookie", "tvfe_boss_uuid=77ce84b0af77c334; video_platform=2; video_guid=f5aa0d25e4aa0b62; pgv_pvid=8172569500; pgv_info=ssid=s4222040415")
                        .upJson(gson.toJson(body))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String result = response.body();
                                try {
                                    JSONObject jo = new JSONObject(result);
                                    JSONArray ja = jo.getJSONObject("data").getJSONArray("CardList");
                                    jo = ja.getJSONObject(1);
                                    ja = jo.getJSONObject("children_list").getJSONObject("list").getJSONArray("cards");
                                    String json = ja.toString();

                                    List<XunVideo> list = gson.fromJson(json, new TypeToken<List<XunVideo>>() {
                                    });
                                    List<Video> res = list.stream().map(it -> {
                                        Video vd = new Video();
                                        vd.id = it.params.cid;
                                        vd.title = it.params.title;
                                        vd.description = it.params.secondTitle;
                                        vd.score = null == it.params.opinionScore ? 0 : Float.parseFloat(it.params.opinionScore);
                                        vd.imgCover = it.params.newPicVt;
                                        vd.pageUrl = "https://v.qq.com/x/cover/" + it.params.cid + ".html";
                                        vd.channel = "腾讯";
                                        vd.type = type;
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

    public String getHtmlPage(String videoId) {
        String pageUrl = "https://v.qq.com/x/cover/" + videoId + ".html";
        OkGo.<String>get(pageUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String html = response.body();
                        // 提取 __pinia 全局变量
                        String pinia = html.replaceAll(".*?<script>.*?window.__pinia=(.*?)<script>.*?", "$1");
                        LogUtils.i(pinia);
                    }
                });
        return "";
    }
}
