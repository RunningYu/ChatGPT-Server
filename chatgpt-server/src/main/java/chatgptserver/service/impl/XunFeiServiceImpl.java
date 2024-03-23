package chatgptserver.service.impl;

import chatgptserver.Common.ImageUtil;
import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.*;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.ImageResponse;
import chatgptserver.service.OkHttpService;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.XunFeiUtils;
import chatgptserver.utils.xunfei.BigModelNew;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.XunFeiService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.mock.web.MockMultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static chatgptserver.enums.GPTConstants.GPT_KEY_MAP;
import static chatgptserver.enums.GPTConstants.XF_XH_API_SECRET_KEY;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */

@Slf4j
@Service
public class XunFeiServiceImpl implements XunFeiService {

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private OkHttpService okHttpService;


    /**
     * 讯飞星火：图片理解
     */
    @Override
    public SseEmitter xfImageUnderstand(Long threadId, String image, String question) {
        log.info("XunFeiServiceImpl xfImageUnderstand threadId:[{}], image:[{}], question:[{}]", threadId, image, question);
        XunFeiUtils.imageUnderstandFlagMap.put(Thread.currentThread().getId(), false);
        XunFeiUtils.imageUnderstandResponseMap.put(Thread.currentThread().getId(), "");
        SseEmitter sseEmitter = SseUtils.sseEmittersMap.get(threadId);
        String totalResponse = "";

        try {
            // 构建鉴权url
            String authUrl = getAuthUrl(GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL,
                    GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY),
                    GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY));
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            for (int i = 0; i < 1; i++) {
                WebSocket webSocket = client.newWebSocket(request, new BigModelNew(threadId, question, i + "", false));
            }

            while (true) {
                boolean closeFlag = XunFeiUtils.imageUnderstandFlagMap.get(Thread.currentThread().getId());
                XunFeiUtils.imageUnderstandFlagMap.put(Thread.currentThread().getId(), false);
                if (!closeFlag) {
//                    Thread.sleep(200);
//                    String response = XunFeiUtils.imageUnderstandResponseMap.get(threadId);
//                    XunFeiUtils.imageUnderstandResponseMap.put(Thread.currentThread().getId(), "");
//                    if (!response.equals("") && response != null) {
//                        System.out.println("--->[" + response + "]");
//                        totalResponse.append(response);
//                        sseEmitter.send(SseEmitter.event().comment(response));
//                    }
                } else {
                    totalResponse = XunFeiUtils.imageUnderstandTotalResponseMap.get(threadId);
                    log.info("XunFeiServiceImpl xfImageUnderstand totalResponse:[{}]", totalResponse);
                    return sseEmitter;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * todo: 上传到MinIO返回的url是下载url，想弄成浏览url
     * 讯飞星火：图片生成
     */
    @Override
    public JsonResult xfImageCreate(String content) {
        log.info("XunFeiServiceImpl xfImageCreate content:[{}]", content);
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setHeader(new Header(GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY)));
        try {
            // 构建鉴权url
            String authUrl = getPicAuthUrl(GPTConstants.XF_XH_PICTURE_CREATE_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(XF_XH_API_SECRET_KEY));
            System.out.println("hostUrl:" + GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL);
            System.out.println("APPID:" + jsonRootBean.getHeader().getApp_id());
            System.out.println("APPID:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY));
            System.out.println("APISecret:" + GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY));
            System.out.println("authUrl:" + authUrl);
            Text text1 = new Text("user");
            text1.setContent(content);
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
            log.info("XunFeiServiceImpl pictureCreate jsonRootBean:[{}]", jsonRootBean);

            String responseStr = okHttpService.makePostRequest(authUrl, JSON.toJSONString(jsonRootBean));
            ImageResponse imageResponse = XunFeiUtils.buildImageResponse();
            imageResponse = JSON.parseObject(responseStr, ImageResponse.class);
            String imageBase64Str = imageResponse.getPayload().getChoices().getText().get(0).getContent();
            File imageFile = ImageUtil.convertBase64StrToImage(imageBase64Str, System.currentTimeMillis() + "AiPicture.jpg");
            MultipartFile multipartFile = new MockMultipartFile("file", imageFile.getName(), "image", new FileInputStream(imageFile));

            UploadResponse imageUrlResponse = minioUtil.uploadFile(multipartFile, "file");
            String imageUrl = imageUrlResponse.getMinIoUrl();
            log.info("XunFeiServiceImpl pictureCreate imageUrl:[{}]", imageUrl);

            return JsonResult.success(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

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

