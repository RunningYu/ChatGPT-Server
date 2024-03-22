package chatgptserver;

import chatgptserver.bean.dto.WenXin.ImageFlagDTO;
import chatgptserver.enums.GPTConstants;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static chatgptserver.enums.GPTConstants.GPT_KEY_MAP;

//@SpringBootTest
class XunFeiImageUnderstandTests {

    ThreadLocal<Boolean> flagLocal = new ThreadLocal<>();

    /**
     * 讯飞星火：图片理解
     * @throws Exception
     */
    @Test
    void test() throws Exception {
        ImageFlagDTO imageFlag = new ImageFlagDTO();
        // 个性化参数入口，如果是并发使用，可以在这里模拟
        while (true){
            if(imageFlag.getTotalFlag()){
//                Scanner scanner=new Scanner(System.in);
                System.out.print("我：");
//                totalFlag=false;
                imageFlag.setTotalFlag(false);
//                NewQuestion=scanner.nextLine();
                imageFlag.setNewQuestion("描述一下这张图片");
                // 构建鉴权url
                String authUrl = getAuthUrl(GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY) , GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY));
                OkHttpClient client = new OkHttpClient.Builder().build();
                String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
                Request request = new Request.Builder().url(url).build();
                for (int i = 0; i < 1; i++) {
//                    totalAnswer="";
                    imageFlag.setTotalAnswer("");
                    imageFlag.setUserId(i + "");
                    imageFlag.setWsCloseFlag(false);
                    WebSocket webSocket = client.newWebSocket(request, new BigModelNew("描述一下这张图片", i + "", false));
                }
            }else{
                Thread.sleep(200);
            }
        }
    }

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
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
