package chatgptserver.day;


import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.ImageResponse;
import chatgptserver.service.OkHttpService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
// 主函数入口
public class MainApplication {

    @Autowired
    private OkHttpService okHttpService;

    public static final String hostUrl = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";
    public static final String appid = "f6b93318";
    public static final String apiSecret = "NjU2ZTc5ZmNkZWI5NmQwYWI1MDJiMzg4";
    public static final String apiKey = "df704f2ff951c364c9bac34536811256";

    @Test
    public void test() throws Exception {
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        // URL地址正确
        System.err.println(authUrl);
        String json = "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"" + appid + "\",\n" +
                "    \"uid\": \"" + UUID.randomUUID().toString().substring(0, 15) + "\"\n" +
                "  },\n" +
                "  \"parameter\": {\n" +
                "    \"chat\": {\n" +
                "      \"domain\": \"s291394db\",\n" +
                "      \"temperature\": 0.5,\n" +
                "      \"max_tokens\": 4096,\n" +
                "      \"width\": 1024,\n" +
                "      \"height\": 1024\n" +
                "    }\n" +
                "  },\n" +
                "  \"payload\": {\n" +
                "    \"message\": {\n" +
                "      \"text\": [\n" +
                "        {\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"帮我画一个小鸟\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        // 发起Post请求
        System.err.println(json);
//        String res = MyUtil.doPostJson(authUrl, null, json);
        String res = okHttpService.makePostRequest(authUrl, json);
        ImageResponse imageResponse = JSONObject.parseObject(res, ImageResponse.class);
        System.out.println("--------------------------------------------------------------------------------------------");
//        System.out.println(res);
        System.out.println(imageResponse.getPayload().getChoices().getText().getContent());
        System.out.println("--------------------------------------------------------------------------------------------");
//        System.out.println(res);
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // date="Thu, 12 Oct 2023 03:05:28 GMT";
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "POST " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }
}