package chatgptserver.service.impl;

import chatgptserver.Common.ImageUtil;
import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.ao.UploadResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.*;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.imageCreate.ImageResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.ChapterContents;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.Chapters;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptCoverResponseDTO;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import chatgptserver.bean.po.MessagesPO;
import chatgptserver.dao.MessageMapper;
import chatgptserver.dao.UserMapper;
import chatgptserver.enums.CharacterConstants;
import chatgptserver.service.*;
import chatgptserver.utils.*;
import chatgptserver.utils.xunfei.BigModelNew;
import chatgptserver.enums.GPTConstants;
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
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

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
    public JsonResult xfImageUnderstand(Long threadId, MultipartFile image, String content, String token, String chatCode, Boolean isRebuild, String cid) {
        log.info("XunFeiServiceImpl xfImageUnderstand threadId:[{}], image:[{}], question:[{}], token:[{}], chatCode:[{}]", threadId, image, content, token, chatCode);
        Date questionTime = new Date();
        String realContent = content;
        if (image == null && (content == null || "".equals(content)) && isRebuild == false) {
            log.info("XunFeiServiceImpl xfImageUnderstand 图片和文字不能同时为空");

            return JsonResult.error(500, "图片和文字不能同时为空");
        }
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
            // 封装多轮对话的请求body
            List<Text> imageUnderstandRequest = buildImageUnderstandRequest(chatCode, image, content, isRebuild);
            for (int i = 0; i < 1; i++) {
                WebSocket webSocket = client.newWebSocket(request, new BigModelNew(threadId, imageUnderstandRequest, i + "", false));
            }

            while (true) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(cid)) {
                    StorageUtils.stopRequestMap.remove(cid);

                    return JsonResult.success();
                }
                boolean closeFlag = XunFeiUtils.imageUnderstandFlagMap.get(Thread.currentThread().getId());
                XunFeiUtils.imageUnderstandFlagMap.put(Thread.currentThread().getId(), false);
                if (!closeFlag) {
                    Thread.sleep(200);
                } else {
                    totalResponse = XunFeiUtils.imageUnderstandTotalResponseMap.get(threadId);
                    log.info("XunFeiServiceImpl xfImageUnderstand totalResponse:[{}]", totalResponse);
                    String userCode = userService.getUserCodeByToken(token);
                    MessagesAO response = messageService.buildMessageAO(userCode, chatCode, content, totalResponse, questionTime);
                    String question = "";
                    // 再次检查是否要取消生成
                    if (StorageUtils.stopRequestMap.containsKey(cid)) {
                        StorageUtils.stopRequestMap.remove(cid);

                        return JsonResult.success();
                    }
                    if (image != null) {
                        // 三次检查是否要取消生成
                        if (StorageUtils.stopRequestMap.containsKey(cid)) {
                            StorageUtils.stopRequestMap.remove(cid);

                            return JsonResult.success();
                        }
                        UploadResponse uploadResponse = minioUtil.uploadFile(image, "file");
                        String imageUrl = uploadResponse.getMinIoUrl();
                        response.setImage(uploadResponse.getMinIoUrl());
                        if (realContent == null || "".equals(realContent)) {
                            question = imageUrl;
                        } else {
                            question = imageUrl + "\n\n" + realContent;
                        }

                        if (isRebuild) {
                            messageService.recordHistory("", chatCode, "", totalResponse, isRebuild, questionTime);
                        } else {
                            messageService.recordHistoryWithImage(userCode, chatCode, imageUrl, question, totalResponse, questionTime);
                        }
                    } else {
                        question = content;
                        if (isRebuild) {
                            messageService.recordHistory("", chatCode, "", totalResponse, isRebuild, questionTime);
                        } else {
                            messageService.recordHistoryWithImage(userCode, chatCode, "0", question, totalResponse, questionTime);
                        }
                    }
                    MessagesAO result = messageService.buildMessageAO(userCode, chatCode, question, totalResponse, questionTime);

                    return JsonResult.success(result);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private List<Text> buildImageUnderstandRequest(String chatCode, MultipartFile image, String content, Boolean isRebuild) {
        List<Text> requestList = new ArrayList<>();
        String base64Image = "";
        content = MessageUtils.buildContent(content);
        content = ("".equals(content) ? CharacterConstants.DEFAULT_CONTENT : content);
        String iUrl = null;
        if (isRebuild) {
            MessagesPO messagesPO = messageMapper.getUpdateMessagePO(chatCode);
            if (messagesPO.getImage() != null && messagesPO.getImage().contains("http")) {
                iUrl = messagesPO.getImage();
                log.info("WenXinServiceImpl wenXinImageUnderstand iUrl:[{}]", iUrl);
//                String iUrl = content.split("\n")[0];
                base64Image = ImageUtil.imageUrlToBase64(iUrl);
            }
        }
        // 如果图片为空，则表示多轮对话
//        if (Objects.isNull(image) && !contentsSplit[0].contains("http://124.71.110.30:9000/")) {
        if (Objects.isNull(image) && iUrl == null) {
            // 获取第一轮对话
            MessagesPO messagesFistChat = messageMapper.getTongYiQuestionFistChat(chatCode);
            log.info("XunFeiServiceImpl buildImageUnderstandRequest messagesFistChat:[{}]", messagesFistChat);
            // 图片URL --> base64
            base64Image = ImageUtil.imageUrlToBase64(messagesFistChat.getImage());
            requestList.add(new Text("user", base64Image, "image"));
            String question = messagesFistChat.getQuestion().split("\n")[messagesFistChat.getQuestion().split("\n").length - 1];
            requestList.add(new Text("user",question, "text"));
            requestList.add(new Text("assistant", messagesFistChat.getReplication(), "text"));

            List<MessagesPO> historyLis = messageMapper.getTongYiMultipleQuestionHistory(chatCode, messagesFistChat.getId());
            for (MessagesPO history : historyLis) {
                question = messagesFistChat.getQuestion().split("\n")[messagesFistChat.getQuestion().split("\n").length - 1];
                requestList.add(new Text("user", question, "text"));
                requestList.add(new Text("assistant", history.getReplication(), "text"));
            }
        } else {
            if (image != null) {
                base64Image = ImageUtil.imageMultipartFileToBase64(image);
            }
//            } else {
//                String iUrl = content.split("\n")[0];
//                base64Image = ImageUtil.imageUrlToBase64(iUrl);
//            }
            requestList.add(new Text("user", base64Image, "image"));
        }
        requestList.add(new Text("user", content, "text"));
        log.info("XunFeiServiceImpl buildImageUnderstandRequest requestList:[{}]", requestList);

        return requestList;
    }

    @Override
    public JsonResult xfPptCreate(String content, String token, String chatCode, Boolean isRebuild, String cid) {
        log.info("XunFeiServiceImpl xfPptCreate content:[{}], token:[{}], chatCode:[{}], isRebuild:[{}], cid:[{}]", content, token, chatCode, isRebuild, cid);
        Date questionTime = new Date();
        if (isRebuild == false && (content == null || "".equals(content))) {
            log.info("XunFeiServiceImpl xfPptCreate 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
        String pptUrl = "", coverImgSrc = "", outLineStr = "";
        String userCode = userService.getUserCodeByToken(token);
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
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }

        try {
            // 查询PPT模板信息
            String templateResult = client.getTemplateList(appId, ts, signature);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate templateResult:[{}]", templateResult);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");

            // 发送生成PPT请求
            String query = content;
            String resp = client.createPPT(appId, ts, signature, query);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate resp:[{}]", resp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse response = JSON.parseObject(resp, CreateResponse.class);

            // 利用sid查询PPT生成进度
            int progress = 0;
            ProgressResponse progressResponse;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(cid)) {
                    StorageUtils.stopRequestMap.remove(cid);

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, response.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);

                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }

            // 大纲生成
            String outlineQuery = query;
            String outlineResp = client.createOutline(appId, ts, signature,outlineQuery);
            System.out.println(outlineResp);
            CreateResponse outlineResponse = JSON.parseObject(outlineResp, CreateResponse.class);
            // 封装大纲
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            PptOutlineResponse outline = JSON.parseObject(outlineResponse.getData().getOutline(), PptOutlineResponse.class);
            outLineStr = PptUtils.buildOutLineMD(outline);
            System.out.println("生成的大纲如下：");
            log.info("XunFeiServiceImpl xfPptCreate outline:[{}]", outline);
//            log.info("XunFeiServiceImpl xfPptCreate Outline:[{}]", outlineResponse.getData().getOutline());
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");

            // 基于sid和大纲生成ppt
            String sidResp = client.createPptBySid(appId, ts, signature, outlineResponse.getData().getSid());
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate sidResp1:[{}]", sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            sidResp = client.createPptBySid(appId, ts, signature, outlineResponse.getData().getSid());
            System.out.println(sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate sidResp2:[{}]", sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");

            sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            progress = 0;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(cid)) {
                    StorageUtils.stopRequestMap.remove(cid);

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, sidResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);
                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }
            // 基于大纲生成ppt
            String pptResp = client.createPptByOutline(appId, ts, signature, outlineQuery, outlineResponse.getData().getOutline());
            // 解析 PPT封面
            PptCoverResponseDTO coverRes = JSON.parseObject(pptResp, PptCoverResponseDTO.class);
            coverImgSrc = coverRes.getData().getCoverImgSrc();
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate pptResp:[{}]", pptResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse pptResponse = JSON.parseObject(pptResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            progress = 0;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(cid)) {
                    StorageUtils.stopRequestMap.remove(cid);

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, pptResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);
                pptUrl = progressResponse.getData().getPptUrl();
                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }
            String replication = outLineStr + "\n" + coverImgSrc + "\n\n" + pptUrl + "\n\n" + GPTConstants.RESULT_CREATE_TAG;
            MessagesAO responseAO = messageService.buildMessageAO(userCode, chatCode, content, replication, questionTime);
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }
            messageService.recordHistory(userCode, chatCode, content, replication, isRebuild, questionTime);

            return JsonResult.success(responseAO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }


    /**
     * 讯飞星火：图片生成
     */
    @Override
    public JsonResult xfImageCreate(String content, String token, String chatCode, Boolean isRebuild, String cid) {
        log.info("XunFeiServiceImpl xfImageCreate content:[{}], token:[{}], chatCode:[{}], isRebuild:[{}], cid:[{}]", content, token, chatCode, isRebuild, cid);
        Date questionTime = new Date();
        if (isRebuild == false && (content == null || "".equals(content))) {
            log.info("XunFeiServiceImpl xfImageCreate 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
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
            // 检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(cid)) {
                StorageUtils.stopRequestMap.remove(cid);

                return JsonResult.success();
            }
            ImageResponse imageResponse = XunFeiUtils.buildImageResponse();
            imageResponse = JSON.parseObject(responseStr, ImageResponse.class);
            String imageBase64Str = imageResponse.getPayload().getChoices().getText().get(0).getContent();
            File imageFile = ImageUtil.convertBase64StrToImage(imageBase64Str, System.currentTimeMillis() + "AiPicture.jpg");
            MultipartFile multipartFile = new MockMultipartFile("file", imageFile.getName(), "image/png", new FileInputStream(imageFile));
            UploadResponse imageUrlResponse = minioUtil.uploadFile(multipartFile, "file");
            imageUrl = imageUrlResponse.getMinIoUrl();
            log.info("XunFeiServiceImpl pictureCreate imageUrl:[{}]", imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("调通义千问图片生成接口异常！");
        }
        // 再次检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }
        String userCode = userService.getUserCodeByToken(token);
        String replication = imageUrl + "\n\n" + GPTConstants.RESULT_CREATE_TAG;
        MessagesAO response = messageService.buildMessageAO(userCode, chatCode, content, replication, questionTime);
        // 保存聊天记录
        messageService.recordHistory(userCode, chatCode, content, replication, isRebuild, questionTime);

        return JsonResult.success(response);
    }

    /**
     * 讯飞星火：文本问答
     */
    @Override
    public JsonResult xfQuestion(Long threadId, QuestionRequestAO requestAO) {
        log.info("XunFeiServiceImpl xfQuestion threadId:[{}], requestAO:[{}]", threadId, requestAO);
        Date questionTime = new Date();
        if (requestAO.getIsRebuild() == false && (requestAO.getContent() == null || "".equals(requestAO.getContent()))) {
            log.info("XunFeiServiceImpl xfQuestion 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
        String totalResponse = "";
        XunFeiUtils.questionFlagMap.put(threadId, false);
        XunFeiUtils.questionTotalResponseMap.put(threadId, "");
        MessagesAO response = new MessagesAO();
        boolean flag = true;
        List<MessagesPO> historyList = new ArrayList<>();
        if (requestAO.getUserCode() != null && !"".equals(requestAO.getUserCode())) {
            historyList = messageMapper.getWenXinHistory(requestAO.getChatCode());
        }
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
            // 检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(requestAO.getCid())) {
                StorageUtils.stopRequestMap.remove(requestAO.getCid());

                return null;
            }
            boolean closeFlag = XunFeiUtils.questionFlagMap.get(threadId);
            if (!closeFlag) {
                // Thread.sleep(200);
            } else {
                totalResponse = XunFeiUtils.questionTotalResponseMap.get(threadId);
                log.info("XunFeiServiceImpl xfQuestion totalResponse:[{}]", totalResponse);
                response = messageService.buildMessageAO(requestAO.getUserCode(), requestAO.getChatCode(), requestAO.getContent(), totalResponse, questionTime);
                log.info("XunFeiServiceImpl xfQuestion response:[{}]", response);
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(requestAO.getCid())) {
                    StorageUtils.stopRequestMap.remove(requestAO.getCid());

                    return null;
                }
                messageService.recordHistory(requestAO.getUserCode(), requestAO.getChatCode(), requestAO.getContent(), totalResponse, requestAO.getIsRebuild(), questionTime);

                break;
            }
        }

        return JsonResult.success(response);
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

