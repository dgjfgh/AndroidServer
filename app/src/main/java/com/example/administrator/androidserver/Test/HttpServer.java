package com.example.administrator.androidserver.Test;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * http服务提供类
 *
 * @author sunkai
 * @2014-9-9 上午11:07:10
 */
public class HttpServer extends NanoHTTPD {

    public HttpServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        String serverName = null;
        String serverType = null;
        String serverAction = null;
        int start = uri.indexOf("/");
        start++;
        int end;
        if (-1 != start) {
            end = uri.indexOf("/", start);
            if (-1 == end) {
                end = uri.length();
            }
            serverName = uri.substring(start, end);
            start = end;
        }
        if (-1 != start) {
            start++;
            end = uri.indexOf("/", start);
            if (-1 == end) {
                end = uri.length();
            }
            if (start < end) {
                serverType = uri.substring(start, end);
            }
            start = end;
        }

        if (uri.length() != start) {
            start++;
            end = uri.length();
            if (-1 != end) {
                serverAction = uri.substring(start, end);
            }
        }

        if (Method.POST.equals(method)) {
            Map<String, String> files = new HashMap<String, String>();
            try {
                session.parseBody(files);
            } catch (IOException e) {
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            } catch (ResponseException e) {
                return new Response(e.getStatus(), MIME_PLAINTEXT, e.getMessage());
            }
        } else if (Method.GET.equals(method)) {

        }
        System.out.println(uri);
        System.out.println(method);
        System.out.println(session.getParms());
        System.out.println(serverAction + "---" + serverType + "---" + serverName);
        if (null == serverType || null == serverName) {
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404,NOT FOUND!");
        }
        if (!Constant.serverName.equals(serverName)) {
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404,NOT FOUND!");
        }
        if ("GETIMAGE".equals(serverType.toUpperCase(Locale.getDefault()))) {
            String fileName = session.getParms().get("filename");
            String filePath = "" + "/" + fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                Log.d("IMAGE", filePath);
                return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404,NOT FOUND!");
            }
            try {
                return new Response(Response.Status.OK, NanoHTTPD.MIME_IMG, new FileInputStream(filePath));
            } catch (FileNotFoundException e) {
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + e.getMessage());
            }
        }

        return new Response(Response.Status.OK, NanoHTTPD.MIME_JSON, getResponse(serverAction, session.getParms()));
    }

    private String getResponse(String serverAction, Map<String, String> parameters) {
        String result = "";
        boolean havaParm = false;
        WebServerAPI webServerAPI = new WebServerAPI();
        Class<WebServerAPI> clazz;
        try {
            clazz = WebServerAPI.class;//Class.forName(Config.WEBSERVICE_API_CLASS);
            java.lang.reflect.Method m = null;
            try {
                m = clazz.getMethod(serverAction, Map.class);
                havaParm = true;
            } catch (NoSuchMethodException e) {
                m = clazz.getMethod(serverAction);
            }
            if (havaParm) {
                result = m.invoke(webServerAPI, parameters).toString();
            } else {
                result = m.invoke(webServerAPI).toString();
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "";
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return "";
        }

        return result;
    }
}
