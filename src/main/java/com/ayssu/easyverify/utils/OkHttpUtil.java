package com.ayssu.easyverify.utils;


import jakarta.annotation.Nullable;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 通用工具类（5.x）
 */
public final class OkHttpUtil {

    private static final OkHttpUtil INSTANCE = new OkHttpUtil();

    private final OkHttpClient client;

    private OkHttpUtil() {

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .build();
    }

    public static OkHttpUtil get() {
        return INSTANCE;
    }

    /* ========================== 同步 ========================== */

    public String get(String url) throws IOException {
        return get(url, null, null);
    }

    public String get(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }
        Request.Builder builder = new Request.Builder().url(urlBuilder.build());
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    public String postJson(String url, String json) throws IOException {
        return postJson(url, json, null);
    }

    public String postJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    public String postForm(String url, Map<String, String> params) throws IOException {
        return postForm(url, params, null);
    }

    public String postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        FormBody.Builder fb = new FormBody.Builder();
        if (params != null) params.forEach(fb::add);
        Request.Builder builder = new Request.Builder().url(url).post(fb.build());
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    public String upload(String url,
                         Map<String, String> params,
                         String fileKey,
                         File file) throws IOException {
        return upload(url, params, fileKey, file, null);
    }

    public String upload(String url,
                         Map<String, String> params,
                         String fileKey,
                         File file,
                         @Nullable Map<String, String> headers) throws IOException {
        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileKey, file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")));
        if (params != null) params.forEach(mb::addFormDataPart);
        Request.Builder builder = new Request.Builder().url(url).post(mb.build());
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    public String putJson(String url, String json) throws IOException {
        return putJson(url, json, null);
    }

    public String putJson(String url, String json, Map<String, String> headers) throws IOException {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder().url(url).put(body);
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    public String delete(String url) throws IOException {
        return delete(url, null);
    }

    public String delete(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).delete();
        addHeaders(builder, headers);
        return exec(builder.build());
    }

    /* ========================== 异步 ========================== */

    public void getAsync(String url, Map<String, String> headers, Map<String, String> params, Callback cb) {
        HttpUrl.Builder ub = HttpUrl.parse(url).newBuilder();
        if (params != null) params.forEach(ub::addQueryParameter);
        Request.Builder builder = new Request.Builder().url(ub.build());
        addHeaders(builder, headers);
        client.newCall(builder.build()).enqueue(cb);
    }

    public void postJsonAsync(String url, String json, Map<String, String> headers, Callback cb) {
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder().url(url).post(body);
        addHeaders(builder, headers);
        client.newCall(builder.build()).enqueue(cb);
    }

    /* ========================== 内部 ========================== */

    private String exec(Request request) throws IOException {
        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Unexpected code " + resp.code() + " " + resp.message());
            }
            return resp.body() == null ? "" : resp.body().string();
        }
    }

    private void addHeaders(Request.Builder builder, @Nullable Map<String, String> headers) {
        if (headers != null) headers.forEach(builder::addHeader);
    }
}