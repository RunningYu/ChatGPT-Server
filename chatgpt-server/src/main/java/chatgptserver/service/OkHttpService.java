package chatgptserver.service;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/3 15:00
 */
@Component
public class OkHttpService {

//    private final OkHttpClient client = new OkHttpClient();

    private final OkHttpClient client = new OkHttpClient.Builder()
            // 连接超时
            .connectTimeout(120, TimeUnit.SECONDS)
            //
            .callTimeout(120, TimeUnit.SECONDS)
            // 读超时
            .readTimeout(120, TimeUnit.SECONDS)
            // 写超时
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();

    /**
     * Get Request
     */
    public String makeGetRequest(String apiUrl) throws IOException {
        Request request = new Request.Builder().url(apiUrl).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected response: " + response.code());
            }
        }
    }

    /**
     * Post Request
     */
    public String makePostRequest(String apiUrl, String requestBody) throws IOException{
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
        Request request = new Request.Builder().url(apiUrl).post(body).build();

//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .url(baseUrl)
//                .addHeader("Content-Type", "application/json")
//                .post(requestBody)
//                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            else {
                System.out.println(response.code() + " " + response.message());
                throw new IOException("Unexpected response: " + response.code());
            }
        }
    }

    public String makePostRequest(String apiUrl, String requestBody, String authorization) throws IOException{
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
        Request request = new Request.Builder().
                url(apiUrl).
                addHeader("Authorization", authorization).
                post(body).
                build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            else {
                System.out.println(response.code() + " " + response.message());
                throw new IOException("Unexpected response: " + response.code());
            }
        }
    }

    public String makePostRequest(String apiUrl, String requestBody, String authorization, String enable) throws IOException {
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
        Request request = new Request.Builder().
                url(apiUrl).
                addHeader("Authorization", authorization).
                addHeader("X-DashScope-Async", enable).
                post(body).
                build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
            else {
                System.out.println(response.code() + " " + response.message());
                throw new IOException("Unexpected response: " + response.code());
            }
        }
    }

    public String makeGetRequest(String apiUrl, String authorization) throws IOException {
        Request request = new Request.Builder().
                url(apiUrl).
                addHeader("Authorization", authorization).
                build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected response: " + response.code());
            }
        }
    }
}