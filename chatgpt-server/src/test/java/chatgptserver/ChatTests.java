package chatgptserver;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.WenXin.WXAccessTokenRspDTO;
import chatgptserver.bean.dto.WenXin.WenXinReqMessagesDTO;
import chatgptserver.bean.dto.WenXin.WenXinRequestBodyDTO;
import chatgptserver.bean.dto.WenXin.WenXinRspDTO;
import chatgptserver.bean.dto.XunFeiXingHuo.*;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.ImageResponse;
import chatgptserver.enums.GPTConstants;
import chatgptserver.Common.ImageUtil;
import chatgptserver.service.OkHttpService;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.XunFeiUtils;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static chatgptserver.enums.GPTConstants.*;

@SpringBootTest
class ChatTests {

    @Autowired
    private MinioUtil minioUtil;

    public static final Gson gson = new Gson();

    @Autowired
    private OkHttpService okHttpService;

    /**
     *
     # 步骤一，获取access_token，替换下列示例中的API Key与Secret Key
     curl 'https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API Key]&client_secret=[Secret Key]'

     # 步骤二，调用本文API，使用步骤一获取的access_token，替换下列示例中的”调用接口获取的access_token“
     curl -X POST 'https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=[步骤一调用接口获取的access_token]' -d '{
     "messages": [
     {"role":"user","content":"介绍一下你自己"}
     ]
     }' | iconv -f utf-8 -t utf-8
     */

    /**
     * 4.0
     */
//    private String wenXinGetAccess_tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s";
    /**
     * 8k
     */
    private String wenXinGetAccess_tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s";

    private String apiKey = "";
    private String secretKey = "";

    private String wenXinUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_bot_8k?access_token=%s";
//    private String wenXinUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=%s";

    public static String totalAnswer=""; // 大模型的答案汇总

    @Test
    void okhttpTest() throws IOException {
        String url = String.format(GPTConstants.WEN_XIN_GET_ACCESS_TOKEN_URL, GPT_KEY_MAP.get(GPTConstants.WEN_XIN_API_KEY_NAME), GPT_KEY_MAP.get(GPTConstants.WEN_XIN_SECRET_KEY_NAME));
        String accessToken = okHttpService.makeGetRequest(url);
        String token = "24.3ae0d7871e91ff6b732516e3529e7fcd.2592000.1706860173.282335-46317365";
        System.out.println("url：" + url);
        System.out.println("accessTokenRsp: " + accessToken);
        WXAccessTokenRspDTO accessTokenResponse = JSON.parseObject(accessToken, WXAccessTokenRspDTO.class);
        System.out.println("accessToken：" + accessTokenResponse.getAccess_token());
        String url1 = String.format(GPTConstants.WEN_XIN_ASK_URL, accessTokenResponse.getAccess_token());
        List<WenXinReqMessagesDTO> messagesList = new ArrayList<>();
        WenXinReqMessagesDTO message = new WenXinReqMessagesDTO();
        message.setRole("user");
//        message.setContent("海贼王和柯南这两部动漫哪个更受出名？");
        message.setContent("帮我写一篇关于观看《爱情公寓》的1000字观后感");
        messagesList.add(message);
        WenXinRequestBodyDTO body = new WenXinRequestBodyDTO(messagesList);

        String requestBody = JSON.toJSONString(body);
        System.out.println("requestBody：" + requestBody);
        String responseStr = okHttpService.makePostRequest(url1, requestBody);
        WenXinRspDTO wenXinRspDTO = JSON.parseObject(responseStr, WenXinRspDTO.class);
        System.out.println("response: " + wenXinRspDTO.getResult());

    }

    @Test
    public void XunFeiPictureUnderstandTest() throws Exception {
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setHeader(new Header(GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY)));
        // 构建鉴权url
        String authUrl = getPicAuthUrl(GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(XF_XH_API_SECRET_KEY));
//        String authUrl = createUrl(GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL);
        System.out.println("hostUrl:" + GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL);
        System.out.println("APPID:" + jsonRootBean.getHeader().getApp_id());
        System.out.println("APPID:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY));
        System.out.println("APISecret:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY));
        System.out.println("authUrl:" + authUrl);
        String imageBase64 = Base64.getEncoder().encodeToString(ImageUtil.read("src\\main\\resources\\images\\1.png"));
        Text text1 = new Text("user");
        text1.setContent_type(imageBase64);
        Text text2 = new Text("user");
        text2.setContent("帮我描述一下这个图片的内容");
        List<Text> textList = new ArrayList<>();
        textList.add(text1);
        textList.add(text2);
        jsonRootBean.setPayload(new Payload(new Message(textList)));
        String responseStr = okHttpService.makePostRequest(authUrl, JSON.toJSONString(jsonRootBean));
        System.out.println("responseStr:" + responseStr);

//        OkHttpClient client = new OkHttpClient.Builder().build();
//        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
//        Request request = new Request.Builder().url(url).build();
//        for (int i = 0; i < 1; i++) {
//            totalAnswer="";
//            WebSocket webSocket = client.newWebSocket(request, new BigModelNew(i + "",
//                    false));
//        }


    }

    /** 鉴权方法 */
    public String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        System.out.println("---->date: " +date); // 对 Fri, 12 Jan 2024 06:36:20 GMT
        // 拼接
        String temp = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        System.out.println("---->temp"); // 对
        System.out.println(temp);
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(temp.getBytes(StandardCharsets.UTF_8));
        System.out.println("---->tmpSha: " + Arrays.toString(hexDigits));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        System.out.println("---->sha: " + sha );
        // System.err.println(sha);
        // 拼接
        String authorization_origin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        System.out.println("---->authorization_origin: " + authorization_origin );
        String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes(StandardCharsets.UTF_8));
//        String authorization = "2024-01-12 15:11:23.506 | INFO     | __main__:create_url:54 - signature_sha b'+\\xb0\\x1b\\x1f\\xffa\\x84c\\x98\\xae\\xc0\\n\\x1c\\x9f\\xcb5\\x01\\xad\\x03\\x0e{\\xf9\\x19P\\xa0\\x0bsVG\\x96\\xddF' type <class 'bytes'>\n" +
//                "2024-01-12 15:11:23.509 | INFO     | __main__:create_url:58 - signature_sha_base64 K7AbH/9hhGOYrsAKHJ/LNQGtAw57+RlQoAtzVkeW3UY= type <class 'str'>\n" +
//                "2024-01-12 15:11:23.509 | INFO     | __main__:create_url:62 - authorization_origin api_key=\"df704f2ff951c364c9bac34536811256\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"K7AbH/9hhGOYrsAKHJ/LNQGtAw57+RlQoAtzVkeW3UY=\" type <class 'str'>\n" +
//                "2024-01-12 15:11:23.509 | INFO     | __main__:create_url:66 - authorization YXBpX2tleT0iZGY3MDRmMmZmOTUxYzM2NGM5YmFjMzQ1MzY4MTEyNTYiLCBhbGdvcml0aG09ImhtYWMtc2hhMjU2IiwgaGVhZGVycz0iaG9zdCBkYXRlIHJlcXVlc3QtbGluZSIsIHNpZ25hdHVyZT0iSzdBYkgvOWhoR09ZcnNBS0hKL0xOUUd0QXc1NytSbFFvQXR6VmtlVzNVWT0i type <class 'str'>";
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", authorization).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        System.out.println("---->httpUrl: " + httpUrl.toString());
        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }


    /**
     * 讯飞星火：图片生成
     */
    @Test
    public void pictureCreate() throws Exception {
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setHeader(new Header(GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY)));
        // 构建鉴权url
        String authUrl = getPicAuthUrl(GPTConstants.XF_XH_PICTURE_CREATE_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(XF_XH_API_SECRET_KEY));
//        String authUrl = createUrl(GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL);
        System.out.println("hostUrl:" + GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL);
        System.out.println("APPID:" + jsonRootBean.getHeader().getApp_id());
        System.out.println("APPID:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY));
        System.out.println("APISecret:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY));
        System.out.println("authUrl:" + authUrl);
        String imageBase64 = Base64.getEncoder().encodeToString(ImageUtil.read("src\\main\\resources\\images\\1.png"));
        Text text1 = new Text("user");
        text1.setContent("帮我画一座山");
        List<Text> textList = new ArrayList<>();
        textList.add(text1);
        jsonRootBean.setPayload(new Payload(new Message(textList)));
        Chat chat = new Chat();
        chat.setDomain("s291394db");
        chat.setTemperature(0.5);
        chat.setMax_tokens(4096);
        chat.setWidth(1024);
        chat.setHeight(1024);
        jsonRootBean.setParameter(new Parameter(chat));
        System.out.println("---------------------------------------------------------");
        System.out.println(JSON.toJSONString(jsonRootBean));
        System.out.println("---------------------------------------------------------");
//        String json = "{\n" +
//                "  \"header\": {\n" +
//                "    \"app_id\": \"" + GPT_KEY_MAP.get(XF_XH_APPID_KEY) + "\",\n" +
//                "    \"uid\": \"" + UUID.randomUUID().toString().substring(0, 15) + "\"\n" +
//                "  },\n" +
//                "  \"parameter\": {\n" +
//                "    \"chat\": {\n" +
//                "      \"domain\": \"s291394db\",\n" +
//                "      \"temperature\": 0.5,\n" +
//                "      \"max_tokens\": 4096,\n" +
//                "      \"width\": 1024,\n" +
//                "      \"height\": 1024\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"payload\": {\n" +
//                "    \"message\": {\n" +
//                "      \"text\": [\n" +
//                "        {\n" +
//                "          \"role\": \"user\",\n" +
//                "          \"content\": \"帮我画一个小鸟\"\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
        String responseStr = okHttpService.makePostRequest(authUrl, JSON.toJSONString(jsonRootBean));
        ImageResponse imageResponse = XunFeiUtils.buildImageResponse();
        imageResponse = JSON.parseObject(responseStr, ImageResponse.class);

        String imageBase64Str = imageResponse.getPayload().getChoices().getText().get(0).getContent();
        File imageFile = ImageUtil.convertBase64StrToImage(imageBase64Str,  System.currentTimeMillis() + "山.jpg");

        MultipartFile multipartFile = new MockMultipartFile("file", imageFile.getName(), "image", new FileInputStream(imageFile));
        UploadResponse imageUrlResponse = minioUtil.uploadFile(multipartFile, "file");
        String imageUrl = imageUrlResponse.getMinIoUrl();
        System.out.println("-------------------------------------------------------------------");
        System.out.println("imageUrl: " + imageUrl);
        System.out.println("-------------------------------------------------------------------");

    }


    public static void main(String[] args) throws Exception {
        // 这是你的API密钥（也称为秘密密钥）
        String apiSecret = "your_api_secret";

        // 这是你要签名的数据（也称为消息）
        String dataToSign = "tmp_signature";

        // 创建HMAC-SHA256算法的实例
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        // 对数据进行签名
        byte[] rawHmac = sha256_HMAC.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getEncoder().encodeToString(rawHmac);

        System.out.println("Signature: " + signature);
    }



    private String host;
    private String path;
    private String APIKey = XF_XH_API_KEY;
    private String APISecret = XF_XH_API_SECRET_KEY;
    private String ImageUnderstanding_url = XF_XH_PICTURE_UNDERSTAND_URL;

    // Constructor and other methods

    public String createUrl(String hostUrl) throws UnsupportedEncodingException, MalformedURLException {
        URL url = new URL(hostUrl);
        host = url.getHost();
        path = url.getPath();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = formatter.format(new Date());

        String signatureOrigin = "host: " + host + "\n"
                + "date: " + date + "\n"
                + "GET " + path + " HTTP/1.1";

        String signatureSha = hmacSha256(APISecret, signatureOrigin);

        String signatureShaBase64 = Base64.getEncoder().encodeToString(signatureSha.getBytes(StandardCharsets.UTF_8));

        String authorizationOrigin = "api_key=\"" + APIKey + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signatureShaBase64 + "\"";

        String authorization = Base64.getEncoder().encodeToString(authorizationOrigin.getBytes(StandardCharsets.UTF_8));

        Map<String, String> params = new HashMap<>();
        params.put("authorization", authorization);
        params.put("date", date);
        params.put("host", host);

        return ImageUnderstanding_url + "?" + getParamsString(params);
    }

    private String hmacSha256(String secret, String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating HMAC SHA 256", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), String.valueOf(StandardCharsets.UTF_8)));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), String.valueOf(StandardCharsets.UTF_8)));
            result.append("&");
        }
        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }


    // 鉴权方法
    public static String getPicAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
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

//https://spark-api.cn-huabei-1.xf-yun.com/v2.1/image?authorization=YXBpX2tleT0iZGY3MDRmMmZmOTUxYzM2NGM5YmFjMzQ1MzY4MTEyNTYiLCBhbGdvcml0aG09ImhtYWMtc2hhMjU2IiwgaGVhZGVycz0iaG9zdCBkYXRlIHJlcXVlc3QtbGluZSIsIHNpZ25hdHVyZT0iRnpsSkdndnBjRXRYeHgrZVN3WVhmMEUzRzZZWko1TDlheVpnd3JkQVU2OD0i&date=Fri%2C%2012%20Jan%202024%2002%3A49%3A54%20GMT&host=spark-api.cn-huabei-1.xf-yun.com
//  wss://spark-api.xf-yun.com/v1.1/chat?authorization=YXBpX2tleT0iYWRkZDIyNzJiNmQ4YjdjOGFiZGQ3OTUzMTQyMGNhM2IiLCBhbGdvcml0aG09ImhtYWMtc2hhMjU2IiwgaGVhZGVycz0iaG9zdCBkYXRlIHJlcXVlc3QtbGluZSIsIHNpZ25hdHVyZT0iejVnSGR1M3B4VlY0QURNeWs0Njd3T1dEUTlxNkJRelIzbmZNVGpjL0RhUT0i&date=Fri%2C+05+May+2023+10%3A43%3A39+GMT&host=spark-api.xf-yun.com


