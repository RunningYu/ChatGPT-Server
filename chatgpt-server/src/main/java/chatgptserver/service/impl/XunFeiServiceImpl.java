package chatgptserver.service.impl;

import chatgptserver.Common.ImageUtil;
import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.*;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.ImageResponse;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.service.MessageService;
import chatgptserver.service.OkHttpService;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.XunFeiUtils;
import chatgptserver.utils.xunfei.BigModelNew;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.XunFeiService;
import chatgptserver.utils.xunfei.XunFeiWenDaBigModelNew;
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
import java.io.IOException;
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
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private OkHttpService okHttpService;


    /**
     * 讯飞星火：图片理解
     */
    @Override
    public SseEmitter xfImageUnderstand(Long threadId, MultipartFile image, String content, String userCode, String chatCode) {
        log.info("XunFeiServiceImpl xfImageUnderstand threadId:[{}], image:[{}], question:[{}], userCode:[{}], chatCode:[{}]", threadId, image, content, userCode, chatCode);
        XunFeiUtils.imageUnderstandFlagMap.put(threadId, false);
        XunFeiUtils.imageUnderstandResponseMap.put(threadId, "");
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
                WebSocket webSocket = client.newWebSocket(request, new BigModelNew(threadId, image, content, i + "", false));
            }

            while (true) {
                boolean closeFlag = XunFeiUtils.imageUnderstandFlagMap.get(Thread.currentThread().getId());
                XunFeiUtils.imageUnderstandFlagMap.put(Thread.currentThread().getId(), false);
                if (!closeFlag) {
                    Thread.sleep(200);
                } else {
                    totalResponse = XunFeiUtils.imageUnderstandTotalResponseMap.get(threadId);
                    log.info("XunFeiServiceImpl xfImageUnderstand totalResponse:[{}]", totalResponse);
                    UploadResponse uploadResponse = minioUtil.uploadFile(image, "file");
                    messageService.recordHistoryWithImage(userCode, chatCode, uploadResponse.getMinIoUrl(), content, totalResponse);
                    return sseEmitter;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public String xfPptCreate(String content, String userCode, String chatCode) {
        log.info("XunFeiServiceImpl xfImageUnderstand content:[{}], userCode:[{}], chatCode:[{}]", content, userCode, chatCode);
        String pptUrl = "";

        // 输入个人appId
        String appId = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY);
        String secret = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY);
        long timestamp = System.currentTimeMillis() / 1000;
        String ts = String.valueOf(timestamp);
        // 获得鉴权信息
        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(appId, secret, timestamp);
        System.out.println(signature);

        // 建立链接
        ApiClient client = new ApiClient(GPTConstants.XF_XH_PPT_CREATE_URL);

        try {
            // 查询PPT模板信息
            String templateResult = client.getTemplateList(appId, ts, signature);
            System.out.println(templateResult);

            // 发送生成PPT请求
            String query = content;
            String resp = client.createPPT(appId, ts, signature,query);
            System.out.println(resp);
            CreateResponse response = JSON.parseObject(resp, CreateResponse.class);

            // 利用sid查询PPT生成进度
            int progress = 0;
            ProgressResponse progressResponse;
            while (progress < 100) {
                String progressResult = client.checkProgress(appId, ts, signature, response.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println(progressResult);

                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }

            // 大纲生成
            String outlineQuery = query;
            String outlineResp = client.createOutline(appId, ts, signature,outlineQuery);
            System.out.println(outlineResp);
            CreateResponse outlineResponse = JSON.parseObject(outlineResp, CreateResponse.class);
            System.out.println("生成的大纲如下：");
            System.out.println(outlineResponse.getData().getOutline());

            // 基于sid和大纲生成ppt
            String sidResp = client.createPptBySid(appId, ts, signature, outlineResponse.getData().getSid());
            System.out.println(sidResp);
            CreateResponse sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            sidResp = client.createPptBySid(appId, ts, signature, outlineResponse.getData().getSid());
            System.out.println(sidResp);
            sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            progress = 0;
            while (progress < 100) {
                String progressResult = client.checkProgress(appId, ts, signature, sidResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println(progressResult);
                if (progress < 100) {
                    Thread.sleep(5000);
                } 
            }

            // 基于大纲生成ppt
            String pptResp = client.createPptByOutline(appId, ts, signature, outlineQuery, outlineResponse.getData().getOutline());
            System.out.println(pptResp);
            CreateResponse pptResponse = JSON.parseObject(pptResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            progress = 0;
            while (progress < 100) {
                String progressResult = client.checkProgress(appId, ts, signature, pptResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println(progressResult);
                pptUrl = progressResponse.getData().getPptUrl();
                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }
            messageService.recordHistory(userCode, chatCode, content, pptUrl);

            return pptUrl;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * 讯飞星火：图片生成
     */
    @Override
    public JsonResult xfImageCreate(String content, String userCode, String chatCode) {
        log.info("XunFeiServiceImpl xfImageCreate content:[{}], userCode:[{}], chatCode:[{}]", content, userCode, chatCode);
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setHeader(new Header(GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY)));
        String imageUrl = "";
        try {
            // 构建鉴权url
            String authUrl = getPicAuthUrl(GPTConstants.XF_XH_PICTURE_CREATE_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(XF_XH_API_SECRET_KEY));
            log.info("XunFeiServiceImpl xfImageCreate hostUrl:[{}], APPID:[{}], APPKEY:[{}], APISecret:[{}], authUrl:[{}]", GPTConstants.XF_XH_PICTURE_UNDERSTAND_URL,
                    jsonRootBean.getHeader().getApp_id(), GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY), authUrl);
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
            MultipartFile multipartFile = new MockMultipartFile("file", imageFile.getName(), "image/png", new FileInputStream(imageFile));

            UploadResponse imageUrlResponse = minioUtil.uploadFile(multipartFile, "file");
            imageUrl = imageUrlResponse.getMinIoUrl();
            log.info("XunFeiServiceImpl pictureCreate imageUrl:[{}]", imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("条用通义千问图片生成接口异常！");
        }
        // 保存聊天记录
        messageService.recordHistory(userCode, chatCode, content, imageUrl);

        return JsonResult.success(imageUrl);
    }

    /**
     * 讯飞星火：文本问答
     */
    @Override
    public SseEmitter xfQuestion(Long threadId, QuestionRequestAO requestAO) {
        log.info("XunFeiServiceImpl xfQuestion threadId:[{}], requestAO:[{}]", threadId, requestAO);
        XunFeiUtils.questionFlagMap.put(threadId, false);
        XunFeiUtils.questionTotalResponseMap.put(threadId, "");
        boolean flag = true;
        List<MessagesPO> historyList = messageMapper.getWenXinHistory(requestAO.getChatCode());

        try {
            // 构建鉴权url
            String authUrl = getAuthUrl(GPTConstants.XF_XH_QUESTION_URL, GPT_KEY_MAP.get(GPTConstants.XF_XH_API_KEY), GPT_KEY_MAP.get(XF_XH_API_SECRET_KEY));
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            for (int i = 0; i < 1; i++) {
                WebSocket webSocket = client.newWebSocket(request, new XunFeiWenDaBigModelNew(threadId, requestAO.getContent(), historyList, i + "",
                        false));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        while (true) {
            boolean closeFlag = XunFeiUtils.questionFlagMap.get(threadId);
            if (!closeFlag) {
                // Thread.sleep(200);

            } else {
                String totalResponse = XunFeiUtils.questionTotalResponseMap.get(threadId);
                log.info("XunFeiServiceImpl xfQuestion totalResponse:[{}]", totalResponse);
                messageService.recordHistory(requestAO.getUserCode(), requestAO.getChatCode(), requestAO.getContent(), totalResponse);
                break;
            }
        }

        return SseUtils.sseEmittersMap.get(threadId);
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

