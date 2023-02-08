package com.yhy.mz.tv.widget.web;

import androidx.appcompat.app.AppCompatActivity;

import com.yhy.mz.tv.parser.Parser;

/**
 * Created on 2023-02-08 12:49
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ParserWebView {

    void attach(AppCompatActivity activity, Parser parser, String url);

    void start();

    void stop(boolean destroy);
}
