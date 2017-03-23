package com.example.administrator.androidserver;

import android.app.Application;

import com.example.administrator.androidserver.Test.HttpServer;

import java.io.IOException;

/**
 * Created by majianghua on 2016/12/5.
 */
public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            new HttpServer(8080).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
